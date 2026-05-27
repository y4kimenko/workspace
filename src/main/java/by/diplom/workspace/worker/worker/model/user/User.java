package by.diplom.workspace.worker.worker.model.user;

import by.diplom.workspace.booking.model.workplace.WorkplaceBooking;
import by.diplom.workspace.worker.email.exception.CannotDeleteLastEmailException;
import by.diplom.workspace.worker.email.exception.CannotDeletePrimaryEmailException;
import by.diplom.workspace.worker.email.exception.EmailNotFoundException;
import by.diplom.workspace.worker.email.exception.PrimaryEmailNotFoundException;
import by.diplom.workspace.worker.email.model.UserEmail;
import by.diplom.workspace.worker.favorite.model.FavoritePlace;
import by.diplom.workspace.worker.notification.component.EmailSender;
import by.diplom.workspace.worker.notification.model.UserNotificationSettings;
import by.diplom.workspace.worker.position.model.DepartmentPosition;
import by.diplom.workspace.worker.worker.model.user.time.TimeZoneAware;
import by.diplom.workspace.worker.worker.model.user.time.TimeZoneSupport;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class User implements TimeZoneAware {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String nickname; //+ user, -admin

    @Column(name = "password_hash", nullable = false)
    private String passwordHash; //+ user, -admin

    @Column(name = "avatar_path")
    private String avatarPath; //+ user, -admin

    @Column(name = "bio", length = 300)
    private String bio; //+ user, -admin

    @Enumerated(EnumType.STRING)
    @Column(name = "pronoun", length = 20)
    private Pronoun pronoun = Pronoun.NOT_SPECIFIED; //+ user, - admin

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Setter(AccessLevel.NONE)
    @Column(name = "timezone", nullable = false, length = 64)
    private String timeZone = TimeZoneSupport.DEFAULT_TIME_ZONE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_position_id")
    private DepartmentPosition departmentPosition; //+ user, +- admin(создание да, но не управления их у пользователя)

    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private final List<UserEmail> emails = new ArrayList<>(); // +

    @OneToOne(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            optional = false
    )
    private UserNotificationSettings notificationSettings;


    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private final Set<FavoritePlace> favoritePlaces = new HashSet<>();

    @OneToMany(
            mappedBy = "createdBy",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private final List<WorkplaceBooking> bookingsWorkplace = new ArrayList<>();

    protected User(
            String fullName,
            String nickname,
            String passwordHash,
            DepartmentPosition departmentPosition
    ) {
        this.fullName = fullName;
        this.nickname = nickname;
        this.passwordHash = passwordHash;
        this.departmentPosition = departmentPosition;

        this.notificationSettings = new UserNotificationSettings(this);
    }


    public void changeTimeZone(String timeZone) {
        this.timeZone = TimeZoneSupport.validateAndNormalize(timeZone);
    }

    public void resetTimeZoneToDefault() {
        this.timeZone = TimeZoneSupport.DEFAULT_TIME_ZONE;
    }

    public void addEmail(String email, boolean verified, boolean primaryEmail) {
        if (primaryEmail) {
            emails.forEach(userEmail -> userEmail.setPrimaryEmail(false));
        }

        UserEmail userEmail = new UserEmail(this, email, verified, primaryEmail);
        emails.add(userEmail);
    }

    public void removeEmail(String emailToDelete, EmailSender emailSender) {
        if (emails.size() <= 1) {
            throw new CannotDeleteLastEmailException();
        }

        UserEmail target = emails.stream()
                .filter(e -> e.getEmail().equals(emailToDelete))
                .findFirst()
                .orElseThrow(() -> new EmailNotFoundException(emailToDelete));

        if (target.isPrimaryEmail()) {
            throw new CannotDeletePrimaryEmailException();
        }

        boolean wasPublic = target.isPublicEmail();
        boolean wasVerified = target.isVerified();

        String primaryEmail = emails.stream()
                .filter(UserEmail::isPrimaryEmail)
                .map(UserEmail::getEmail)
                .findFirst()
                .orElse(null);

        emails.remove(target);

        // Если удалили единственный публичный —
        // автоматически делаем публичным primary email
        if (wasPublic) {
            boolean hasAnyPublic = emails.stream().anyMatch(UserEmail::isPublicEmail);

            if (!hasAnyPublic) {
                emails.stream()
                        .filter(UserEmail::isPrimaryEmail)
                        .findFirst()
                        .ifPresent(UserEmail::makePublic);
            }
        }

        if (wasVerified && primaryEmail != null) {
            emailSender.sendVerifiedEmailRemovedNotification(
                    primaryEmail,
                    this.fullName,
                    emailToDelete
            );
        }
    }

    public void changePrimaryEmail(String newPrimaryEmail, EmailSender emailSender) {
        UserEmail newPrimary = emails.stream()
                .filter(userEmail -> userEmail.getEmail().equals(newPrimaryEmail))
                .findFirst()
                .orElseThrow(() -> new EmailNotFoundException(newPrimaryEmail));

        // Только подтверждённый email может стать основным
        if (!newPrimary.isVerified()) {
            throw new IllegalStateException(
                    "Нельзя сделать основным неподтверждённый email"
            );
        }

        UserEmail oldPrimary = emails.stream()
                .filter(UserEmail::isPrimaryEmail)
                .findFirst()
                .orElse(null);

        if (newPrimary.isPrimaryEmail()) {
            return;
        }

        for (UserEmail userEmail : emails) {
            userEmail.revokePrimary();
        }

        newPrimary.makePrimary();

        if (oldPrimary != null) {
            emailSender.sendPrimaryEmailChangedNotification(
                    oldPrimary.getEmail(),
                    this.fullName,
                    newPrimaryEmail
            );
        }
    }

    public void changePublicEmail(String newPublicEmail) {
        UserEmail newPublic = emails.stream()
                .filter(userEmail -> userEmail.getEmail().equals(newPublicEmail))
                .findFirst()
                .orElseThrow(() -> new EmailNotFoundException(newPublicEmail));

        // Только подтверждённый email может стать публичным
        if (!newPublic.isVerified()) {
            throw new IllegalStateException(
                    "Нельзя сделать публичным неподтверждённый email"
            );
        }

        if (newPublic.isPublicEmail()) {
            return;
        }

        for (UserEmail userEmail : emails) {
            userEmail.revokePublic();
        }

        newPublic.makePublic();
    }

    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }

    public void changePronoun(Pronoun pronoun) {
        this.pronoun = pronoun != null ? pronoun : Pronoun.NOT_SPECIFIED;
    }

    public void changePassword(String newPasswordHash, EmailSender emailSender) {
        emailSender.sendPasswordChangedNotification(getPrimaryEmailAddress(), this.fullName);
        this.passwordHash = newPasswordHash;
    }


    public String getPrimaryEmailAddress() {
        return emails.stream()
                .filter(UserEmail::isPrimaryEmail)
                .map(UserEmail::getEmail)
                .findFirst().orElseThrow(
                        () -> new PrimaryEmailNotFoundException(getId())
                );
    }

    public ZoneId getZoneId() {
        return ZoneId.of(this.timeZone);
    }
}
