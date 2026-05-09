package by.diplom.workspace.worker.repository;

import by.diplom.workspace.worker.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByNickname(String nickname);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.emails WHERE u.id = :id")
    Optional<User> findByIdWithEmails(@Param("id") UUID id);
}
