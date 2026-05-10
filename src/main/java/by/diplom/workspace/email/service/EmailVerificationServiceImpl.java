package by.diplom.workspace.email.service;

import by.diplom.workspace.email.component.EmailSender;
import by.diplom.workspace.email.dto.response.UserEmailResponseDto;
import by.diplom.workspace.email.exception.EmailAlreadyExistsException;
import by.diplom.workspace.email.exception.EmailNotFoundException;
import by.diplom.workspace.email.exception.InvalidVerificationCodeException;
import by.diplom.workspace.email.mapper.UserEmailMapper;
import by.diplom.workspace.email.model.EmailVerificationToken;
import by.diplom.workspace.email.repository.EmailVerificationTokenRepository;
import by.diplom.workspace.worker.exception.UserNotFoundException;
import by.diplom.workspace.worker.model.user.User;
import by.diplom.workspace.email.model.UserEmail;
import by.diplom.workspace.email.repository.UserEmailRepository;
import by.diplom.workspace.worker.repository.UserRepository;
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

    // SecureRandom — криптографически стойкий генератор, важно для кодов
    private final SecureRandom secureRandom = new SecureRandom();

    // Шаг 1: добавить email и отправить код
    @Override
    @Transactional
    public void addEmailAndSendCode(UUID userId, String email) {

        // 1. Проверяем, что email ещё не занят в системе
        if (userEmailRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(email);
        }

        // 2. Находим пользователя
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // 3. Добавляем email через метод сущности (verified = false)
        user.addEmail(email, false, false);

        // 4. Нужно получить сохранённый UserEmail для создания токена.
        //    flush() заставляет Hibernate выполнить INSERT прямо сейчас,
        //    не дожидаясь конца транзакции — чтобы мы могли найти запись по email.
        userEmailRepository.flush();

        UserEmail userEmail = userEmailRepository.findByEmailAndUserId(email, userId)
                .orElseThrow(() -> new EmailNotFoundException(email));

        // 5. Если уже есть старый токен для этого email — удаляем (повторная отправка)
        tokenRepository.deleteByUserEmailId(userEmail.getId());

        // 6. Генерируем 6-значный код и сохраняем токен
        String code = generateCode();
        tokenRepository.save(new EmailVerificationToken(userEmail, code));

        // 7. Отправляем письмо
        emailSender.sendVerificationCode(email, code);
    }

    // Шаг 2: подтвердить email по коду
    @Override
    @Transactional
    public void verifyEmail(UUID userId, String email, String code) {

        // 1. Находим UserEmail текущего пользователя
        UserEmail userEmail = userEmailRepository.findByEmailAndUserId(email, userId)
                .orElseThrow(() -> new EmailNotFoundException(email));

        // 2. Проверяем, не подтверждён ли уже
        if (userEmail.isVerified()) {
            return; // идемпотентно — не бросаем ошибку
        }

        // 3. Находим токен
        EmailVerificationToken token = tokenRepository
                .findByUserEmailId(userEmail.getId())
                .orElseThrow(InvalidVerificationCodeException::new);

        // 4. Проверяем код и срок действия
        if (token.isExpired() || !token.getCode().equals(code)) {
            throw new InvalidVerificationCodeException();
        }

        // 5. Помечаем email как подтверждённый
        userEmail.markAsVerified();

        // 6. Если это первая подтвержденная почта, то делаем ее основной
        if (userEmail.getUser().getEmails().stream().noneMatch(UserEmail::isPrimaryEmail))
            userEmail.makePrimary();

        // 7. Удаляем использованный токен
        tokenRepository.delete(token);
    }

    // Повторная отправка кода
    @Override
    @Transactional
    public void resendCode(UUID userId, String email) {
        // Просто вызываем тот же метод — он удалит старый токен и создаст новый
        addEmailAndSendCode(userId, email);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserEmailResponseDto> getUserEmails(UUID userId) {
        return userEmailRepository.findAllByUserId(userId)
                .stream()
                .map(UserEmailMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public void updatePrimaryEmail(UUID userId, String newPrimaryEmail) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException(userId));

        boolean belongsToUser = user.getEmails().stream()
                .anyMatch(e -> e.getEmail().equals(newPrimaryEmail));

        if (!belongsToUser)
            throw new EmailNotFoundException(newPrimaryEmail);

        user.changePrimaryEmail(newPrimaryEmail);
    }

    @Override
    @Transactional
    public void updatePublicEmail(UUID userId, String newPublicEmail) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException(userId));

        boolean belongsToUser = user.getEmails().stream()
                .anyMatch(e -> e.getEmail().equals(newPublicEmail));

        if (!belongsToUser)
            throw new EmailNotFoundException(newPublicEmail);

        user.changePublicEmail(newPublicEmail);
    }

    @Override
    @Transactional
    public void deleteEmail(UUID userId, String emailToDelete) {
        User user = userRepository.findByIdWithEmails(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // email принадлежит именно этому пользователю?
        boolean belongsToUser = user.getEmails().stream()
                .anyMatch(e -> e.getEmail().equals(emailToDelete));

        if (!belongsToUser) {
            throw new EmailNotFoundException(emailToDelete);
        }

        // Если был неподтверждённый токен — удаляем его явно до удаления email,
        // чтобы не получить ConstraintViolationException из-за FK
        userEmailRepository.findByEmailAndUserId(emailToDelete, userId)
                .ifPresent(userEmail ->
                        tokenRepository.deleteByUserEmailId(userEmail.getId())
                );

        user.removeEmail(emailToDelete);
    }

    // Генерация кода
    private String generateCode() {
        // Число от 100000 до 999999 — всегда 6 цифр
        int code = 100_000 + secureRandom.nextInt(900_000);
        return String.valueOf(code);
    }
}