package by.diplom.workspace.email.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@Table(name = "email_verification_tokens")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailVerificationToken {

    private static final long TTL_MINUTES = 15;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Связь с конкретным email, который подтверждаем
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_email_id", nullable = false, unique = true)
    private UserEmail userEmail;

    @Column(name = "code", nullable = false, length = 6)
    private String code;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    public EmailVerificationToken(UserEmail userEmail, String code) {
        this.userEmail = userEmail;
        this.code = code;
        this.expiresAt = Instant.now().plusSeconds(TTL_MINUTES * 60);
    }

    public boolean isExpired() {
        return Instant.now().isAfter(this.expiresAt);
    }
}