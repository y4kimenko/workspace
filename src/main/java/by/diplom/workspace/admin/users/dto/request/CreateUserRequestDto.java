package by.diplom.workspace.admin.users.dto.request;

import by.diplom.workspace.admin.users.dto.EnumsDto.UserTypeRequest;
import by.diplom.workspace.worker.position.dto.request.DepartmentPositionRequestDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateUserRequestDto(
        @NotBlank
        String fullName,

        @Valid
        @NotNull(message = "Отдел и должность обязательны")
        DepartmentPositionRequestDto departmentPosition,

        @Email @NotBlank
        String email,

        @NotNull
        UserTypeRequest userType
) {
}
