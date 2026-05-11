package by.diplom.workspace.worker.email.service;


import by.diplom.workspace.worker.email.dto.response.UserEmailResponseDto;
import by.diplom.workspace.worker.email.exception.EmailAlreadyExistsException;
import by.diplom.workspace.worker.email.exception.EmailLimitExceededException;
import by.diplom.workspace.worker.email.exception.EmailNotFoundException;
import by.diplom.workspace.worker.email.exception.InvalidVerificationCodeException;
import by.diplom.workspace.worker.email.mapper.UserEmailMapper;
import by.diplom.workspace.worker.email.model.EmailVerificationToken;
import by.diplom.workspace.worker.email.model.UserEmail;
import by.diplom.workspace.worker.email.repository.EmailVerificationTokenRepository;
import by.diplom.workspace.worker.email.repository.UserEmailRepository;
import by.diplom.workspace.worker.notification.component.EmailSender;
import by.diplom.workspace.worker.worker.exception.UserNotFoundException;
import by.diplom.workspace.worker.worker.model.user.User;
import by.diplom.workspace.worker.worker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailVerificationServiceImpl implements EmailVerificationService {

    private final UserRepository userRepository;
    private final UserEmailRepository userEmailRepository;
    private final EmailVerificationTokenRepository tokenRepository;
    private final EmailSender emailSender;
    private final EmailVerificationExpiryService expiryService;

    // SecureRandom — криптографически стойкий генератор, важно для кодов
    private final SecureRandom secureRandom = new SecureRandom();

    // ── Шаг 1: добавить email и отправить код ────────────────────────────────

    @Override
    @Transactional
    public void addEmailAndSendCode(UUID userId, String email) {

        // 1. Проверяем, что email ещё не занят в системе глобально
        if (userEmailRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(email);
        }

        // 2. Находим пользователя
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (user.getEmails().size() == UserEmail.MAX_EMAIL_COUNT)
            throw new EmailLimitExceededException(UserEmail.MAX_EMAIL_COUNT);

        // 3. Добавляем email через метод сущности (verified = false, primaryEmail = false)
        user.addEmail(email, false, false);

        // 4. flush() — Hibernate выполняет INSERT прямо сейчас, чтобы мы могли
        //    получить сохранённый UserEmail по email + userId для создания токена
        userEmailRepository.flush();

        UserEmail userEmail = userEmailRepository.findByEmailAndUserId(email, userId)
                .orElseThrow(() -> new EmailNotFoundException(email));

        // 5. Если уже есть старый токен — удаляем (сценарий повторной отправки).
        //    Также отменяем старую задачу автоудаления — таймер сбрасывается
        tokenRepository.deleteByUserEmailId(userEmail.getId());
        expiryService.cancelExpiry(userEmail.getId());

        // 6. Генерируем 6-значный код и сохраняем токен (TTL 15 минут)
        String code = generateCode();
        tokenRepository.save(new EmailVerificationToken(userEmail, code));

        // 7. Планируем автоудаление неверифицированной почты через 15 минут
        expiryService.scheduleExpiry(userEmail.getId());

        // 8. Отправляем письмо с кодом
        emailSender.sendVerificationCode(email, code);
    }

    // ── Шаг 2: подтвердить email по коду ────────────────────────────────────

    @Override
    @Transactional
    public void verifyEmail(UUID userId, String email, String code) {

        // 1. Находим UserEmail текущего пользователя
        UserEmail userEmail = userEmailRepository.findByEmailAndUserId(email, userId)
                .orElseThrow(() -> new EmailNotFoundException(email));

        // 2. Идемпотентность — если уже верифицирован, просто выходим
        if (userEmail.isVerified()) {
            return;
        }

        // 3. Находим токен верификации
        EmailVerificationToken token = tokenRepository
                .findByUserEmailId(userEmail.getId())
                .orElseThrow(InvalidVerificationCodeException::new);

        // 4. Проверяем срок действия и совпадение кода
        if (token.isExpired() || !token.getCode().equals(code)) {
            throw new InvalidVerificationCodeException();
        }

        // 5. Помечаем email как верифицированный
        userEmail.markAsVerified();

        // 6. Отменяем задачу автоудаления — почта успешно подтверждена
        expiryService.cancelExpiry(userEmail.getId());

        // 7. Отправляем уведомление на основную почту о добавлении новой верифицированной
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));


        emailSender.sendNewVerifiedEmailAddedNotification(
                user.getPrimaryEmailAddress(),
                user.getFullName(),
                email
        );

        // 8. Удаляем использованный токен
        tokenRepository.delete(token);
    }

    // ── Повторная отправка кода ───────────────────────────────────────────────

    @Override
    @Transactional
    public void resendCode(UUID userId, String email) {
        // addEmailAndSendCode: удалит старый токен, отменит старую задачу,
        // создаст новый токен и запланирует новое автоудаление — таймер сбрасывается
        addEmailAndSendCode(userId, email);
    }

    // ── Список почт пользователя ──────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<UserEmailResponseDto> getUserVerifiedEmails(UUID userId) {
        return userEmailRepository.findAllByUserId(userId)
                .stream()
                .filter(UserEmail::isVerified)
                .map(UserEmailMapper::toResponseDto)
                .toList();
    }

    // ── Список верифицированных почт пользователя ──────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<UserEmailResponseDto> getUserEmails(UUID userId) {
        return userEmailRepository.findAllByUserId(userId)
                .stream()
                .map(UserEmailMapper::toResponseDto)
                .toList();
    }

    // ── Смена основной почты ──────────────────────────────────────────────────

    @Override
    @Transactional
    public void updatePrimaryEmail(UUID userId, String newPrimaryEmail) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        boolean belongsToUser = user.getEmails().stream()
                .anyMatch(e -> e.getEmail().equals(newPrimaryEmail));

        if (!belongsToUser) {
            throw new EmailNotFoundException(newPrimaryEmail);
        }

        // Внутри changePrimaryEmail(email, emailSender) уйдёт уведомление
        // на старую основную почту о смене
        user.changePrimaryEmail(newPrimaryEmail, emailSender);
    }


    // ── Удаление почты ────────────────────────────────────────────────────────

    @Override
    @Transactional
    public void deleteEmail(UUID userId, String emailToDelete) {
        User user = userRepository.findByIdWithEmails(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        boolean belongsToUser = user.getEmails().stream()
                .anyMatch(e -> e.getEmail().equals(emailToDelete));

        if (!belongsToUser) {
            throw new EmailNotFoundException(emailToDelete);
        }

        userEmailRepository.findByEmailAndUserId(emailToDelete, userId)
                .ifPresent(userEmail -> {
                    // Отменяем задачу автоудаления (актуально для неверифицированных)
                    expiryService.cancelExpiry(userEmail.getId());
                    // Удаляем токен верификации явно, чтобы не получить FK violation
                    tokenRepository.deleteByUserEmailId(userEmail.getId());
                });

        // Внутри removeEmail(email, emailSender) уйдёт уведомление
        // на основную почту об удалении верифицированного адреса
        user.removeEmail(emailToDelete, emailSender);
    }

    // ── Утилиты ───────────────────────────────────────────────────────────────

    private String generateCode() {
        // Число от 100000 до 999999 — всегда ровно 6 цифр
        int code = 100_000 + secureRandom.nextInt(900_000);
        return String.valueOf(code);
    }
}