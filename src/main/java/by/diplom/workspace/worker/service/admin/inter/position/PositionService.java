package by.diplom.workspace.worker.service.admin.inter.position;

import by.diplom.workspace.worker.dto.position.request.create.CreatePositionRequestDto;
import by.diplom.workspace.worker.dto.position.request.update.UpdatePositionRequestDto;
import by.diplom.workspace.worker.dto.position.response.PositionResponseDto;

import java.util.List;

public interface PositionService {
    PositionResponseDto createPosition(CreatePositionRequestDto request);
    PositionResponseDto updatePosition(Long id, UpdatePositionRequestDto request);
    void deletePosition(Long id);
    List<PositionResponseDto> getAllPositions();
    PositionResponseDto getPosition(Long id);

}
