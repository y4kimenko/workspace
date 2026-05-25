package by.diplom.workspace.admin.request_registration.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class DepartmentPositionNotFoundException extends RuntimeException {

    public DepartmentPositionNotFoundException(Long departmentId, Long positionId) {
        super("Связка отдела с ID " + departmentId + " и должности с ID " + positionId + " не найдена");
    }
}
