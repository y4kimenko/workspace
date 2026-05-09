package by.diplom.workspace.worker.dto.response;

import by.diplom.workspace.worker.model.user.profile.Pronoun;

import java.util.UUID;

public record UserPronounResponse(
        UUID id,
        Pronoun pronoun,         // "HE_HIM" — для логики на фронтенде
        String displayName       // "он/его" — для отображения
) {
}
