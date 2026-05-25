package by.diplom.workspace.admin.request_registration.service.impl;

import by.diplom.workspace.admin.request_registration.dto.RegistrationRequestDto;
import by.diplom.workspace.admin.request_registration.exception.DepartmentPositionNotFoundException;
import by.diplom.workspace.admin.request_registration.exception.RegistrationRequestAlreadyExistsException;
import by.diplom.workspace.admin.request_registration.exception.RegistrationRequestNotFoundException;
import by.diplom.workspace.admin.request_registration.model.RegistrationRequest;
import by.diplom.workspace.admin.request_registration.model.StatusRegistration;
import by.diplom.workspace.admin.request_registration.repository.RegistrationRequestRepository;
import by.diplom.workspace.admin.request_registration.service.RegistrationService;
import by.diplom.workspace.worker.email.dto.request.VerifyEmailRequestDto;
import by.diplom.workspace.worker.email.exception.EmailAlreadyExistsException;
import by.diplom.workspace.worker.email.exception.InvalidVerificationCodeException;
import by.diplom.workspace.worker.email.model.EmailVerificationToken;
import by.diplom.workspace.worker.email.repository.EmailVerificationTokenRepository;
import by.diplom.workspace.worker.email.repository.UserEmailRepository;
import by.diplom.workspace.worker.notification.component.EmailSender;
import by.diplom.workspace.worker.position.dto.request.DepartmentPositionRequestDto;
import by.diplom.workspace.worker.position.exception.DepartmentNotFoundException;
import by.diplom.workspace.worker.position.exception.PositionNotFoundException;
import by.diplom.workspace.worker.position.model.DepartmentPosition;
import by.diplom.workspace.worker.position.repository.DepartmentPositionRepository;
import by.diplom.workspace.worker.position.repository.DepartmentRepository;
import by.diplom.workspace.worker.position.repository.PositionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

    private final RegistrationRequestRepository registrationRequestRepository;
    private final DepartmentPositionRepository departmentPositionRepository;
    private final DepartmentRepository departmentRepository;
    private final UserEmailRepository userEmailRepository;
    private final EmailVerificationTokenRepository tokenRepository;
    private final EmailSender emailSender;
    private final RegistrationExpiryService registrationExpiryService;

    // SecureRandom – криптографически стойкий генератор, важно для кодов подтверждения
    private final SecureRandom secureRandom = new SecureRandom();
    private final PositionRepository positionRepository;

    // ── Шаг 1: создание заявки и отправка кода подтверждения ─────────────────

    @Override
    @Transactional
    public void createRegistrationRequest(RegistrationRequestDto requestDto) {
        String email = normalizeEmail(requestDto.email());

        // 1. Проверяем, что активной заявки с таким email ещё нет
        if (registrationRequestRepository.existsByEmailAndStatus(email, StatusRegistration.WAITING)) {
            throw new RegistrationRequestAlreadyExistsException(email);
        }

        // 2. Проверяем, что пользователь с таким email ещё не зарегистрирован
        if (userEmailRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(email);
        }

        // 3. Находим связку отдела и должности
        DepartmentPositionRequestDto departmentPositionDto = requestDto.departmentPosition();

        DepartmentPosition departmentPosition = departmentPositionRepository
                .findByDepartment_IdAndPosition_Id(
                        departmentPositionDto.departmentId(),
                        departmentPositionDto.positionId()
                )
                .orElseThrow(() -> new DepartmentPositionNotFoundException(
                        departmentPositionDto.departmentId(),
                        departmentPositionDto.positionId()
                ));

        // 4. Создаём заявку. По умолчанию status = WAITING, emailIsVerified = false
        RegistrationRequest registrationRequest = new RegistrationRequest(
                requestDto.fullName().trim(),
                departmentPosition,
                email
        );

        RegistrationRequest savedRegistrationRequest = registrationRequestRepository.save(registrationRequest);

        // 5. Удаляем старый токен, если он остался после предыдущей попытки
        tokenRepository.deleteByEmail(email);

        // 6. Генерируем 6-значный код и сохраняем токен
        String code = generateCode();
        tokenRepository.save(new EmailVerificationToken(email, code));

        // 7. Отправляем код подтверждения на почту
        emailSender.sendVerificationCode(email, code);

        // 8. Планируем автоудаление заявки, если email не подтвердят за 15 минут
        registrationExpiryService.scheduleExpiry(savedRegistrationRequest.getId());
    }

    // ── Шаг 2: подтверждение email по коду ───────────────────────────────────

    @Override
    @Transactional
    public void verifyEmail(VerifyEmailRequestDto requestDto) {
        String email = normalizeEmail(requestDto.email());

        // 1. Находим активную заявку по email
        RegistrationRequest registrationRequest = registrationRequestRepository
                .findByEmailAndStatus(email, StatusRegistration.WAITING)
                .orElseThrow(() -> new RegistrationRequestNotFoundException(email));

        // 2. Идемпотентность – если email уже подтверждён, просто возвращаем успешный ответ
        if (registrationRequest.isEmailIsVerified()) {
            return;
        }

        // 3. Находим токен подтверждения по email
        EmailVerificationToken token = tokenRepository.findByEmail(email)
                .orElseThrow(InvalidVerificationCodeException::new);

        // 4. Проверяем срок действия токена и совпадение кода
        if (token.isExpired() || !token.getCode().equals(requestDto.code())) {
            throw new InvalidVerificationCodeException();
        }

        // 5. Помечаем email как подтверждённый
        registrationRequest.setEmailIsVerified(true);

        // 6. Отменяем автоудаление заявки
        registrationExpiryService.cancelExpiry(registrationRequest.getId());

        // 7. Удаляем использованный токен
        tokenRepository.delete(token);

        // 8. Отправка сообщения пользователю о том заявка рассматривается
        DepartmentPosition departmentPosition = registrationRequest.getDepartmentPosition();
        emailSender.sendUserCreationRequestReviewNotification(
                registrationRequest.getEmail(),
                registrationRequest.getFullName(),
                departmentRepository.findById(departmentPosition.getDepartment().getId()).orElseThrow(
                        () -> new DepartmentNotFoundException(departmentPosition.getDepartment().getId())
                ).getName(),
                positionRepository.findById(departmentPosition.getPosition().getId()).orElseThrow(
                        () -> new PositionNotFoundException(departmentPosition.getPosition().getId())
                ).getName()
        );
    }

    // ── Утилиты ───────────────────────────────────────────────────────────────

    private String generateCode() {
        // Число от 100000 до 999999 — всегда ровно 6 цифр
        int code = 100_000 + secureRandom.nextInt(900_000);
        return String.valueOf(code);
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
