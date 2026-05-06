package by.diplom.workspace.model.user.settings;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import by.diplom.workspace.model.user.User;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "user_notification_settings")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserNotificationSettings {

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "email_notifications_enabled", nullable = false)
    private boolean emailNotificationsEnabled = true;

    public UserNotificationSettings(User user) {
        this.user = user;
    }
}