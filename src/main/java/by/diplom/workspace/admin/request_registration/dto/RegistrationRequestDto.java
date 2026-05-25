package by.diplom.workspace.admin.request_registration.dto;

import by.diplom.workspace.worker.position.dto.request.DepartmentPositionRequestDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegistrationRequestDto(

        @NotBlank(message = "ФИО обязательно")
        String fullName,

        @Email(message = "Некорректный формат email")
        @NotBlank(message = "Email обязателен")
        String email,

        @Valid
        @NotNull(message = "Отдел и должность обязательны")
        DepartmentPositionRequestDto departmentPosition
) {
}
