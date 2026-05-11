package by.diplom.workspace.favorite.exception;

import java.util.UUID;


public class FavoritePlaceNotFoundException extends RuntimeException {
    public FavoritePlaceNotFoundException(UUID userId, long placeId) {
        super("Место %s не найдено в избранном у пользователя %s".formatted(placeId, userId));
    }
}
