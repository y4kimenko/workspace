package by.diplom.workspace.worker.email.repository;

import by.diplom.workspace.worker.email.model.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, UUID> {

    /**
     * Находит токен по ID связанного UserEmail.
     * Используется при верификации кода.
     */
    Optional<EmailVerificationToken> findByEmail(String email);

    void deleteByEmail(String email);
}
