package by.diplom.workspace.position.mapper;

import by.diplom.workspace.position.dto.response.PositionResponseDto;
import by.diplom.workspace.position.model.Position;

public class PositionMapper {
    public static PositionResponseDto toResponseDto(Position p) {
        return new PositionResponseDto(p.getId(), p.getName());
    }
}
