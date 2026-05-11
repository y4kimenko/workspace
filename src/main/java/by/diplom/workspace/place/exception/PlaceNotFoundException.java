package by.diplom.workspace.place.exception;


public class PlaceNotFoundException extends RuntimeException {
    public PlaceNotFoundException(Long id) {
        super("Место с id " + id + " не найдено");
    }
}
