package by.diplom.workspace.notification.repository;

import by.diplom.workspace.notification.model.UserNotificationSettings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserNotificationSettingsRepository extends JpaRepository<UserNotificationSettings, UUID> {
}