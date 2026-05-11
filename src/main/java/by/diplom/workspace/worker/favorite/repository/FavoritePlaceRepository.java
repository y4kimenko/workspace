package by.diplom.workspace.worker.favorite.repository;

import by.diplom.workspace.worker.favorite.model.FavoritePlace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FavoritePlaceRepository extends JpaRepository<FavoritePlace, UUID> {
    @Query("""
            SELECT fp FROM FavoritePlace fp
            JOIN FETCH fp.place p
            LEFT JOIN FETCH p.equipments
            LEFT JOIN FETCH p.advantages
            WHERE fp.user.id = :userId
            """)
    List<FavoritePlace> findAllByUserId(@Param("userId") UUID userId);

    Optional<FavoritePlace> findByUserIdAndPlaceId(UUID userId, long placeId);

    boolean existsByUserIdAndPlaceId(UUID userId, long placeId);
}
