package by.diplom.workspace.worker.repository;

import by.diplom.workspace.worker.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByNickname(String nickname);
}
