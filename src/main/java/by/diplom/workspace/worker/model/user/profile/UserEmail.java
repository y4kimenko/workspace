package by.diplom.workspace.worker.model.user.profile;

import by.diplom.workspace.worker.model.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(
        name = "user_emails",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_email",
                        columnNames = "email"
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEmail {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "is_verified", nullable = false)
    private boolean verified = false;

    @Column(name = "is_primary", nullable = false)
    private boolean primaryEmail = false;

    @Column(name = "is_public", nullable = false)
    private boolean publicEmail = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public UserEmail(
            User user,
            String email,
            boolean verified,
            boolean primaryEmail
    ) {
        this.user = user;
        this.email = email;
        this.verified = verified;
        this.primaryEmail = primaryEmail;
    }

    public void markAsVerified() {
        this.verified = true;
    }

    public void makePrimary() {
        this.primaryEmail = true;
    }

    public void revokePrimary() {
        this.primaryEmail = false;
    }

    public void makePublic() {
        this.publicEmail = true;
    }

    public void revokePublic() {
        this.publicEmail = false;
    }

}