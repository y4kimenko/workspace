package by.diplom.workspace.admin.request_registration.dto;

import jakarta.validation.constraints.Size;

public record RejectRegistrationRequestDto(
        @Size(max = 1000, message = "Причина отклонения не должна превышать 1000 символов")
        String reason
) {
}
