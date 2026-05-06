package by.diplom.workspace.worker.model.user.profile;

import by.diplom.workspace.worker.model.user.User;
import jakarta.persistence.*;
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
        name = "user_social_links",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_social_platform",
                        columnNames = {"user_id", "platform_id"}
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserSocialLink {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "platform_id", nullable = false)
    private SocialPlatform platform;

    @Column(name = "url", nullable = false, length = 2048)
    private String url;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public UserSocialLink(User user, SocialPlatform platform, String url) {
        this.user = user;
        this.platform = platform;
        this.url = url;
    }
}
