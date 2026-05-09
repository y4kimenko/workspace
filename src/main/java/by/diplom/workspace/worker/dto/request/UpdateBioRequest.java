package by.diplom.workspace.worker.dto.request;

import jakarta.validation.constraints.Max;
public record UpdateBioRequest(
        @Max(value = 300, message = "Био не может быть больше 300 символов")
        String bio
) { }
