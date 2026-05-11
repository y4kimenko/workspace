package by.diplom.workspace.worker.worker.model.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Pronoun {

    NOT_SPECIFIED("Не указано"),
    HE_HIM("он/его"),
    SHE_HER("она/её"),
    THEY_THEM("они/их"),
    CUSTOM("Другое");

    private final String displayName;
}
