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
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailVerificationServiceImpl implements EmailVerificationService {

    private final UserRepository userRepository;
    private final UserEmailRepository userEmailRepository;
    private final EmailVerificationTokenRepository tokenRepository;
    private final EmailSender emailSender;
    private final EmailVerificationExpiryService expiryService;

    // SecureRandom – криптографически стойкий генератор, важно для кодов
    private final SecureRandom secureRandom = new SecureRandom();

    // ── Шаг 1: добавить email ────────────────────────────────────────────────

    @Override
    @Transactional
    public void addEmail(UUID userId, String email) {
        String normalizedEmail = normalizeEmail(email);

        // 1. Проверяем, что email ещё не занят в системе глобально
        if (userEmailRepository.existsByEmail(normalizedEmail)) {
            throw new EmailAlreadyExistsException(normalizedEmail);
        }

        // 2. Находим пользователя
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // 3. Проверяем лимит email-адресов пользователя
        if (user.getEmails().size() == UserEmail.MAX_EMAIL_COUNT) {
            throw new EmailLimitExceededException(UserEmail.MAX_EMAIL_COUNT);
        }

        // 4. Добавляем email через метод сущности
        // verified = false, primaryEmail = false
        user.addEmail(normalizedEmail, false, false);

        // 5. flush() нужен, чтобы запись UserEmail точно появилась в БД
        // и её можно было найти по email + userId
        userEmailRepository.flush();

        // 6. Отправляем код подтверждения
        sendVerificationCode(userId, normalizedEmail);
    }

    // ── Отправка кода подтверждения ──────────────────────────────────────────

    @Override
    @Transactional
    public void sendVerificationCode(UUID userId, String email) {
        String normalizedEmail = normalizeEmail(email);

        // 1. Находим UserEmail текущего пользователя
        UserEmail userEmail = userEmailRepository.findByEmailAndUserId(normalizedEmail, userId)
                .orElseThrow(() -> new EmailNotFoundException(normalizedEmail));

        // 2. Если email уже подтверждён, повторно код не отправляем
        if (userEmail.isVerified()) {
            return;
        }

        // 3. Если уже есть старый токен – удаляем его.
        // Теперь токен связан не с UserEmail, а напрямую с email.
        tokenRepository.deleteByEmail(normalizedEmail);

        // 4. Отменяем старую задачу автоудаления – таймер будет сброшен
        expiryService.cancelExpiry(userEmail.getId());

        // 5. Генерируем 6-значный код и сохраняем токен
        String code = generateCode();
        tokenRepository.save(new EmailVerificationToken(normalizedEmail, code));

        // 6. Планируем автоудаление неверифицированной почты
        expiryService.scheduleExpiry(userEmail.getId());

        // 7. Отправляем письмо с кодом
        emailSender.sendVerificationCode(normalizedEmail, code);
    }

    // ── Шаг 2: подтвердить email по коду ────────────────────────────────────

    @Override
    @Transactional
    public void verifyEmail(UUID userId, String email, String code) {
        String normalizedEmail = normalizeEmail(email);

        // 1. Находим UserEmail текущего пользователя
        UserEmail userEmail = userEmailRepository.findByEmailAndUserId(normalizedEmail, userId)
                .orElseThrow(() -> new EmailNotFoundException(normalizedEmail));

        // 2. Идемпотентность – если уже верифицирован, просто выходим
        if (userEmail.isVerified()) {
            return;
        }

        // 3. Находим токен верификации по email.
        // Раньше здесь был поиск по userEmailId, но теперь токен хранит email.
        EmailVerificationToken token = tokenRepository.findByEmail(normalizedEmail)
                .orElseThrow(InvalidVerificationCodeException::new);

        // 4. Проверяем срок действия и совпадение кода
        if (token.isExpired() || !token.getCode().equals(code)) {
            throw new InvalidVerificationCodeException();
        }

        // 5. Помечаем email как верифицированный
        userEmail.markAsVerified();

        // 6. Отменяем задачу автоудаления – почта успешно подтверждена
        expiryService.cancelExpiry(userEmail.getId());

        // 7. Отправляем уведомление на основную почту о добавлении новой верифицированной
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        emailSender.sendNewVerifiedEmailAddedNotification(
                user.getPrimaryEmailAddress(),
                user.getFullName(),
                normalizedEmail
        );

        // 8. Удаляем использованный токен
        tokenRepository.delete(token);
    }

    // ── Повторная отправка кода ──────────────────────────────────────────────

    @Override
    @Transactional
    public void resendCode(UUID userId, String email) {
        // Важно: нельзя вызывать addEmail(...), потому что email уже существует
        // и проверка userEmailRepository.existsByEmail(...) выбросит исключение.
        // Поэтому здесь только пересоздаём токен и отправляем новый код.
        sendVerificationCode(userId, email);
    }

    // ── Список почт пользователя ─────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<UserEmailResponseDto> getUserVerifiedEmails(UUID userId) {
        return userEmailRepository.findAllByUserId(userId)
                .stream()
                .filter(UserEmail::isVerified)
                .map(UserEmailMapper::toResponseDto)
                .toList();
    }

    // ── Список всех почт пользователя ────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<UserEmailResponseDto> getUserEmails(UUID userId) {
        return userEmailRepository.findAllByUserId(userId)
                .stream()
                .map(UserEmailMapper::toResponseDto)
                .toList();
    }

    // ── Смена основной почты ─────────────────────────────────────────────────

    @Override
    @Transactional
    public void updatePrimaryEmail(UUID userId, String newPrimaryEmail) {
        String normalizedEmail = normalizeEmail(newPrimaryEmail);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        boolean belongsToUser = user.getEmails().stream()
                .anyMatch(email -> email.getEmail().equals(normalizedEmail));

        if (!belongsToUser) {
            throw new EmailNotFoundException(normalizedEmail);
        }

        // Внутри changePrimaryEmail(email, emailSender) уйдёт уведомление
        // на старую основную почту о смене
        user.changePrimaryEmail(normalizedEmail, emailSender);
    }

    // ── Удаление почты ───────────────────────────────────────────────────────

    @Override
    @Transactional
    public void deleteEmail(UUID userId, String emailToDelete) {
        String normalizedEmail = normalizeEmail(emailToDelete);

        User user = userRepository.findByIdWithEmails(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        boolean belongsToUser = user.getEmails().stream()
                .anyMatch(email -> email.getEmail().equals(normalizedEmail));

        if (!belongsToUser) {
            throw new EmailNotFoundException(normalizedEmail);
        }

        userEmailRepository.findByEmailAndUserId(normalizedEmail, userId)
                .ifPresent(userEmail -> {
                    // Отменяем задачу автоудаления
                    expiryService.cancelExpiry(userEmail.getId());

                    // Удаляем токен подтверждения.
                    // Теперь токен удаляется по email, а не по userEmailId.
                    tokenRepository.deleteByEmail(normalizedEmail);
                });

        // Внутри removeEmail(email, emailSender) уйдёт уведомление
        // на основную почту об удалении верифицированного адреса
        user.removeEmail(normalizedEmail, emailSender);
    }

    // ── Утилиты ──────────────────────────────────────────────────────────────

    private String generateCode() {
        // Число от 100000 до 999999 – всегда ровно 6 цифр
        int code = 100_000 + secureRandom.nextInt(900_000);
        return String.valueOf(code);
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}