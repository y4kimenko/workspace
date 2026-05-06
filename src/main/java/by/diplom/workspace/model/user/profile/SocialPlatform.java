package by.diplom.workspace.model.user.profile;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(
        name = "social_platforms",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_social_platform_code",
                        columnNames = "code"
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SocialPlatform {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "name", nullable = false)
    private String name;

    public SocialPlatform(String code, String name) {
        this.code = code;
        this.name = name;
    }
}