package by.diplom.workspace.worker.favorite.service;

import by.diplom.workspace.place.dto.MeetingRoomResponseDto;
import by.diplom.workspace.place.dto.WorkplaceResponseDto;
import by.diplom.workspace.place.exception.PlaceNotFoundException;
import by.diplom.workspace.place.model.place.Place;
import by.diplom.workspace.place.repository.PlaceRepository;
import by.diplom.workspace.worker.favorite.exception.FavoritePlaceAlreadyExistsException;
import by.diplom.workspace.worker.favorite.exception.FavoritePlaceNotFoundException;
import by.diplom.workspace.worker.favorite.model.FavoritePlace;
import by.diplom.workspace.worker.favorite.repository.FavoritePlaceRepository;
import by.diplom.workspace.worker.worker.exception.UserNotFoundException;
import by.diplom.workspace.worker.worker.model.user.User;
import by.diplom.workspace.worker.worker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FavoritePlaceServiceImpl implements FavoritePlaceService {
    private final FavoritePlaceRepository favoritePlaceRepository;
    private final UserRepository userRepository;
    private final PlaceRepository placeRepository;


    @Override
    public List<WorkplaceResponseDto> getFavoritesWorkplace(UUID userId) {
        // TODO
        return List.of();
    }

    @Override
    public List<MeetingRoomResponseDto> getFavoritesMeetingRoom(UUID userId) {
        // TODO
        return List.of();
    }

    @Override
    @Transactional
    public void addToFavorites(UUID userId, Long placeId) {
        if (favoritePlaceRepository.existsByUserIdAndPlaceId(userId, placeId)) {
            throw new FavoritePlaceAlreadyExistsException(userId, placeId);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new PlaceNotFoundException(placeId));

        favoritePlaceRepository.save(new FavoritePlace(user, place));
    }

    @Override
    @Transactional
    public void removeFromFavorites(UUID userId, Long placeId) {
        FavoritePlace favoritePlace = favoritePlaceRepository
                .findByUserIdAndPlaceId(userId, placeId)
                .orElseThrow(() -> new FavoritePlaceNotFoundException(userId, placeId));

        favoritePlaceRepository.delete(favoritePlace);
    }
}
