package by.diplom.workspace.worker.model.user.settings;

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
        name = "themes",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_theme_code",
                        columnNames = "code"
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Theme {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "name", nullable = false)
    private String name;

    public Theme(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
