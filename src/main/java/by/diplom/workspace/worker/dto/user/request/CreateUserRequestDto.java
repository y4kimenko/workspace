package by.diplom.workspace.worker.dto.user.request;

import by.diplom.workspace.worker.dto.user.EnumsDto.UserTypeRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record CreateUserRequestDto(
        @NotBlank
        String fullName,

        @NotNull
        @PositiveOrZero
        Long departmentPositionId,   // должность уже содержит отдел

        @Email @NotBlank
        String email,

        @NotNull
        UserTypeRequest userType
) {
}
