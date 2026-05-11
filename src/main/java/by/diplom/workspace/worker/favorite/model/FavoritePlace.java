package by.diplom.workspace.worker.favorite.model;

import by.diplom.workspace.place.model.place.Place;
import by.diplom.workspace.worker.worker.model.user.User;
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

import java.util.UUID;

@Getter
@Entity
@Table(
        name = "favorite_places",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_favorite_place_user_place",
                        columnNames = {"user_id", "place_id"}
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FavoritePlace {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;

    public FavoritePlace(User user, Place place) {
        if (user == null) {
            throw new IllegalArgumentException("Пользователь не может быть null");
        }

        if (place == null) {
            throw new IllegalArgumentException("Место не может быть null");
        }

        this.user = user;
        this.place = place;
    }
}
