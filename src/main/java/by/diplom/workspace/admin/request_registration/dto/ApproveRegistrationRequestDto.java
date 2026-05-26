package by.diplom.workspace.admin.request_registration.dto;

import by.diplom.workspace.admin.users.dto.EnumsDto.UserTypeRequest;
import by.diplom.workspace.worker.position.dto.request.DepartmentPositionRequestDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ApproveRegistrationRequestDto(
        @NotBlank
        String fullName,

        @Valid
        @NotNull(message = "Отдел и должность обязательны")
        DepartmentPositionRequestDto departmentPosition,

        @NotNull
        UserTypeRequest userType
) {
}
