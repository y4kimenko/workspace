package by.diplom.workspace.email.service;

import by.diplom.workspace.email.dto.response.UserEmailResponseDto;

import java.util.List;
import java.util.UUID;

public interface EmailVerificationService {

    /**
     * Добавляет новый email пользователю (неверифицированный) и отправляет код подтверждения.
     * Если email уже был добавлен ранее — просто пересылает новый код (resend).
     */
    void addEmailAndSendCode(UUID userId, String email);

    /**
     * Подтверждает email по 6-значному коду.
     * Идемпотентен: если email уже верифицирован — ничего не делает.
     */
    void verifyEmail(UUID userId, String email, String code);

    /**
     * Повторная отправка кода подтверждения.
     * Сбрасывает таймер автоудаления (15 минут) на новый.
     */
    void resendCode(UUID userId, String email);

    /**
     * Возвращает список всех email-адресов пользователя.
     */
    List<UserEmailResponseDto> getUserEmails(UUID userId);

    /**
     * Меняет основную почту пользователя.
     * Отправляет уведомление на старую основную почту.
     */
    void updatePrimaryEmail(UUID userId, String newPrimaryEmail);

    /**
     * Меняет публичную почту пользователя.
     */
    void updatePublicEmail(UUID userId, String newPublicEmail);

    /**
     * Удаляет email-адрес пользователя.
     * Нельзя удалить основную почту или единственную почту.
     */
    void deleteEmail(UUID userId, String emailToDelete);
}