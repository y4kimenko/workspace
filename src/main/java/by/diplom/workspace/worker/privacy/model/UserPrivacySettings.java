package by.diplom.workspace.worker.privacy.model;

import by.diplom.workspace.worker.worker.model.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
@Table(name = "user_privacy_settings")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserPrivacySettings {

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "show_booking_to_colleagues", nullable = false)
    private boolean showBookingToColleagues = true;

    @Column(name = "show_presence_status", nullable = false)
    private boolean showPresenceStatus = true;

    public UserPrivacySettings(User user) {
        this.user = user;
    }
}