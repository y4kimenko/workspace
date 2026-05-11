package by.diplom.workspace.position.dto.request.update;

import jakarta.validation.constraints.NotBlank;

public record UpdateDepartmentRequestDto(
        @NotBlank(message = "Название отдела не может быть пустым")
        String name
) {
}
