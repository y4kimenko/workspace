package by.diplom.workspace.worker.dto.profile.request;

import by.diplom.workspace.worker.model.user.Pronoun;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;

public record UpdatePublicProfileRequestDto(
    @Max(value = 300, message = "Био не может быть больше 300 символов")
    String bio,
    @Email(message = "Некорректный формат email")
    String email,
    @NotNull(message = "Местоимение не может быть null — используй NOT_SPECIFIED")
    Pronoun pronoun
) {}
