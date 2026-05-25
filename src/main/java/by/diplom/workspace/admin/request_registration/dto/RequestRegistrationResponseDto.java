package by.diplom.workspace.admin.request_registration.dto;

import by.diplom.workspace.admin.request_registration.model.StatusRegistration;
import by.diplom.workspace.worker.position.dto.response.DepartmentPositionResponseDto;

import java.time.LocalDateTime;

public record RequestRegistrationResponseDto(
        long id,
        String fullName,
        DepartmentPositionResponseDto departmentPosition,
        String email,
        boolean emailIsVerified,
        LocalDateTime createdAt,
        StatusRegistration status
) {
}
