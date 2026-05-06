package by.diplom.workspace.place.repository;

import by.diplom.workspace.place.model.place.Place;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PlaceRepository extends JpaRepository<Place, UUID> {
}