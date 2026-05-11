package by.diplom.workspace.favorite.exception;

import java.util.UUID;

public class FavoritePlaceAlreadyExistsException extends RuntimeException {
    public FavoritePlaceAlreadyExistsException(UUID userId, Long placeId) {
        super("Место %s уже добавлено в избранное пользователем %s".formatted(placeId, userId));
    }
}
