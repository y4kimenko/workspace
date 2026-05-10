package by.diplom.workspace.worker.dto.profile.request;

import by.diplom.workspace.worker.model.user.profile.Pronoun;
import jakarta.validation.constraints.NotNull;

public record UpdatePronounRequestDto(
        @NotNull(message = "Местоимение не может быть null — используй NOT_SPECIFIED")
        Pronoun pronoun
) {
}
