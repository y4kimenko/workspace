package by.diplom.workspace.favorite.service;

import by.diplom.workspace.place.dto.MeetingRoomResponseDto;
import by.diplom.workspace.place.dto.WorkplaceResponseDto;

import java.util.List;
import java.util.UUID;

public interface FavoritePlaceService {
    List<WorkplaceResponseDto> getFavoritesWorkplace(UUID userId);

    List<MeetingRoomResponseDto> getFavoritesMeetingRoom(UUID userId);

    void addToFavorites(UUID userId, Long workplaceId);

    void removeFromFavorites(UUID userId, Long workplaceId);
}
