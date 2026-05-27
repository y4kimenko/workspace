package by.diplom.workspace.admin.users.dto.request;

import by.diplom.workspace.admin.users.dto.EnumsDto.UserTypeRequest;
import by.diplom.workspace.worker.position.dto.request.DepartmentPositionRequestDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserUpdateRequestDto(
        @NotBlank(message = "fullName не может быть пустым")
        @Size(min = 2, max = 255, message = "ФИО должно содержать от 2 до 255 символов")
        String fullName,

        @NotBlank(message = "nickname не может быть пустым")
        @Size(min = 3, max = 50, message = "Никнейм должен содержать от 3 до 50 символов")
        @Pattern(
                regexp = "^[a-zA-Z0-9._-]+$",
                message = "Никнейм может содержать только латинские буквы, цифры, точку, нижнее подчёркивание и дефис"
        )
        String nickName,

        @Valid
        @NotNull(message = "Отдел и должность обязательны")
        DepartmentPositionRequestDto departmentPosition,

        @NotNull(message = "userType не может быть пустым")
        UserTypeRequest userType
) { }
