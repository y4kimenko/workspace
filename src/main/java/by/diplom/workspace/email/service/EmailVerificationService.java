package by.diplom.workspace.email.service;

import by.diplom.workspace.email.dto.response.UserEmailResponse;

import java.util.List;
import java.util.UUID;

public interface EmailVerificationService {
    void addEmailAndSendCode(UUID userId, String email);
    void verifyEmail(UUID userId, String email, String code);
    void resendCode(UUID userId, String email);

    List<UserEmailResponse> getUserEmails(UUID userId);
    void updatePrimaryEmail(UUID userId, String newPrimaryEmail);
    void updatePublicEmail(UUID userId, String newPublicEmail);
    void deleteEmail(UUID userId, String emailToDelete);
}
