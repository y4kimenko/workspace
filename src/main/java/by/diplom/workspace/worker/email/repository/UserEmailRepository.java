package by.diplom.workspace.worker.email.repository;

import by.diplom.workspace.worker.email.model.UserEmail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserEmailRepository extends JpaRepository<UserEmail, UUID> {
    /**
     * Находит email-запись по адресу и владельцу.
     * Используется при создании токена верификации и при верификации.
     */
    @Query("SELECT e FROM UserEmail e WHERE e.email = :email AND e.user.id = :userId")
    Optional<UserEmail> findByEmailAndUserId(
            @Param("email") String email,
            @Param("userId") UUID userId
    );

    /**
     * Проверяет, занят ли email в системе (у любого пользователя).
     */
    boolean existsByEmail(String email);

    /**
     * Возвращает все email-адреса пользователя.
     */
    @Query("SELECT e FROM UserEmail e WHERE e.user.id = :userId")
    List<UserEmail> findAllByUserId(@Param("userId") UUID userId);

    /**
     * Возвращает все неверифицированные email-адреса (любых пользователей).
     * Используется при старте приложения для восстановления задач автоудаления.
     */
    @Query("SELECT e FROM UserEmail e WHERE e.verified = false")
    List<UserEmail> findAllUnverified();
}
