package by.diplom.workspace.worker.position.mapper;

import by.diplom.workspace.worker.position.dto.response.PositionResponseDto;
import by.diplom.workspace.worker.position.model.Position;

public class PositionMapper {
    public static PositionResponseDto toResponseDto(Position p) {
        return new PositionResponseDto(p.getId(), p.getName());
    }
}
