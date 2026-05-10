package by.diplom.workspace.socialLink.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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