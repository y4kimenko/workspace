package by.diplom.workspace.worker.model.user;

import by.diplom.workspace.worker.model.user.profile.position.DepartmentPosition;
import by.diplom.workspace.worker.model.user.profile.SocialPlatform;
import by.diplom.workspace.worker.model.user.profile.UserEmail;
import by.diplom.workspace.worker.model.user.settings.UserAppearanceSettings;
import by.diplom.workspace.worker.model.user.profile.UserSocialLink;
import by.diplom.workspace.worker.model.user.settings.UserNotificationSettings;
import by.diplom.workspace.worker.model.user.settings.UserPrivacySettings;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "avatar_path")
    private String avatarPath;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "timezone", nullable = false)
    private String timezone = "Europe/Minsk";

    @ManyToOne(optional = false)
    @JoinColumn(name = "department_position_id", nullable = false)
    private DepartmentPosition departmentPosition;

    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<UserEmail> emails = new ArrayList<>();

    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<UserSocialLink> socialLinks = new ArrayList<>();

    @OneToOne(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            optional = false
    )
    private UserAppearanceSettings appearanceSettings;

    @OneToOne(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            optional = false
    )
    private UserNotificationSettings notificationSettings;

    @OneToOne(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            optional = false
    )
    private UserPrivacySettings privacySettings;

    public User(
            String fullName,
            String nickname,
            String passwordHash,
            String timezone,
            DepartmentPosition departmentPosition
    ) {
        this.fullName = fullName;
        this.nickname = nickname;
        this.passwordHash = passwordHash;
        this.timezone = timezone;
        this.departmentPosition = departmentPosition;

        this.appearanceSettings = new UserAppearanceSettings(this);
        this.notificationSettings = new UserNotificationSettings(this);
        this.privacySettings = new UserPrivacySettings(this);
    }

    public void changeTimezone(String timezone) {
        if (timezone == null || timezone.isBlank()) {
            throw new IllegalArgumentException("Timezone must not be empty");
        }

        this.timezone = timezone;
    }

    public void addEmail(String email, boolean verified, boolean primaryEmail) {
        if (primaryEmail) {
            emails.forEach(userEmail -> userEmail.setPrimaryEmail(false));
        }

        UserEmail userEmail = new UserEmail(this, email, verified, primaryEmail);
        emails.add(userEmail);
    }

    public void addSocialLink(SocialPlatform platform, String url) {
        UserSocialLink socialLink = new UserSocialLink(this, platform, url);
        socialLinks.add(socialLink);
    }
}
