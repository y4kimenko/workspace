package by.diplom.workspace.email.repository;

import by.diplom.workspace.email.model.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, UUID> {

    Optional<EmailVerificationToken> findByUserEmailId(UUID userEmailId);

    void deleteByUserEmailId(UUID userEmailId);
}
