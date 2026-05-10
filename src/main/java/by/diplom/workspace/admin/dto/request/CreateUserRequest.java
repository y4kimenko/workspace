package by.diplom.workspace.admin.dto.request;

import by.diplom.workspace.admin.enums.UserTypeRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record CreateUserRequest(
        @NotBlank
        String fullName,

        @NotNull
        @PositiveOrZero
        Long departmentPositionId,   // должность уже содержит отдел

        @Email @NotBlank
        String email,

        @NotNull
        UserTypeRequest userType
) {}
