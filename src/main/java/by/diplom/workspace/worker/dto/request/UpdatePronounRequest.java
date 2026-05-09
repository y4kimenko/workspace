package by.diplom.workspace.worker.dto.request;

import by.diplom.workspace.worker.model.user.profile.Pronoun;
import jakarta.validation.constraints.NotNull;

public record UpdatePronounRequest(
        @NotNull(message = "Местоимение не может быть null — используй NOT_SPECIFIED")
        Pronoun pronoun
) { }
