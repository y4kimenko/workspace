package by.diplom.workspace.worker.mapper.position;

import by.diplom.workspace.worker.dto.position.response.PositionResponseDto;
import by.diplom.workspace.worker.model.user.profile.position.Position;

public class PositionMapper {
    public static PositionResponseDto toResponseDto(Position p) {
        return new PositionResponseDto(p.getId(), p.getName());
    }
}
