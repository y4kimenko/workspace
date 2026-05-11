package by.diplom.workspace.email.repository;

import by.diplom.workspace.email.model.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, UUID> {

    /**
     * Находит токен по ID связанного UserEmail.
     * Используется при верификации кода.
     */
    @Query("SELECT t FROM EmailVerificationToken t WHERE t.userEmail.id = :userEmailId")
    Optional<EmailVerificationToken> findByUserEmailId(UUID userEmailId);

    /**
     * Удаляет токен по ID связанного UserEmail.
     * Используется перед повторной отправкой кода и при удалении почты.
     */
    @Modifying
    @Query("DELETE FROM EmailVerificationToken t WHERE t.userEmail.id = :userEmailId")
    void deleteByUserEmailId(UUID userEmailId);
}
