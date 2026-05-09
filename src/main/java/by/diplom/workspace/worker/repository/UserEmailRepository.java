package by.diplom.workspace.worker.repository;

import by.diplom.workspace.worker.model.user.profile.UserEmail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserEmailRepository extends JpaRepository<UserEmail, UUID> {

    Optional<UserEmail> findByEmailAndUserId(String email, UUID userId);

    boolean existsByEmail(String email);

    // Все email'ы пользователя — для дропдауна
    List<UserEmail> findAllByUserId(UUID userId);

    // Только подтверждённые — для валидации
    List<UserEmail> findAllByUserIdAndVerifiedTrue(UUID userId);
}
