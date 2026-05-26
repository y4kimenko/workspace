package by.diplom.workspace.worker.position.service;

import by.diplom.workspace.worker.position.dto.request.create.CreatePositionRequestDto;
import by.diplom.workspace.worker.position.dto.request.update.UpdatePositionRequestDto;
import by.diplom.workspace.worker.position.dto.response.PositionResponseDto;

import java.util.List;

public interface PositionService {
    PositionResponseDto createPosition(CreatePositionRequestDto request);

    PositionResponseDto updatePosition(Long id, UpdatePositionRequestDto request);

    void deletePosition(Long id);

    List<PositionResponseDto> getAllPositions();

    PositionResponseDto getPosition(Long id);

    List<PositionResponseDto> getAllPositionsByDepartamentId(Long departamentId);

}
