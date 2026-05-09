package by.diplom.workspace.worker.model.user.settings;

import by.diplom.workspace.worker.model.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "user_appearance_settings")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserAppearanceSettings {

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "theme_mode", nullable = false)
    private ThemeMode themeMode = ThemeMode.SYSTEM;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selected_theme_id")
    private Theme selectedTheme;

    @Column(name = "high_contrast", nullable = false)
    private boolean highContrast = false;

    public UserAppearanceSettings(User user) {
        this.user = user;
    }

    public void useSystemTheme() {
        this.themeMode = ThemeMode.SYSTEM;
        this.selectedTheme = null;
    }

    public void useManualTheme(Theme theme) {
        if (theme == null) {
            throw new IllegalArgumentException("Theme must not be null");
        }

        this.themeMode = ThemeMode.MANUAL;
        this.selectedTheme = theme;
    }
}