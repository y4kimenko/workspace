package by.diplom.workspace.worker.position.service.impl;

import by.diplom.workspace.worker.position.dto.request.create.CreatePositionRequestDto;
import by.diplom.workspace.worker.position.dto.request.update.UpdatePositionRequestDto;
import by.diplom.workspace.worker.position.dto.response.PositionResponseDto;
import by.diplom.workspace.worker.position.exception.PositionAlreadyExistsException;
import by.diplom.workspace.worker.position.exception.PositionNotFoundException;
import by.diplom.workspace.worker.position.mapper.PositionMapper;
import by.diplom.workspace.worker.position.model.Position;
import by.diplom.workspace.worker.position.repository.DepartmentPositionRepository;
import by.diplom.workspace.worker.position.repository.PositionRepository;
import by.diplom.workspace.worker.position.service.PositionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PositionServiceImpl implements PositionService {
    private final PositionRepository positionRepository;
    private final DepartmentPositionRepository departmentPositionRepository;

    @Transactional
    public PositionResponseDto createPosition(CreatePositionRequestDto request) {
        if (positionRepository.existsByNameIgnoreCase(request.name())) {
            throw new PositionAlreadyExistsException(request.name());
        }

        Position position = new Position(request.name());
        positionRepository.save(position);
        return PositionMapper.toResponseDto(position);
    }

    @Transactional
    public PositionResponseDto updatePosition(Long id, UpdatePositionRequestDto request) {
        Position position = positionRepository.findById(id)
                .orElseThrow(() -> new PositionNotFoundException(id));

        // Проверяем существование другой должности с таким именем (без учёта регистра)
        if (positionRepository.existsByNameIgnoreCaseAndIdNot(request.name(), id)) {
            throw new PositionAlreadyExistsException(request.name());
        }

        position.setName(request.name());
        return PositionMapper.toResponseDto(position);
    }

    @Transactional
    public void deletePosition(Long id) {
        Position position = positionRepository.findById(id)
                .orElseThrow(() -> new PositionNotFoundException(id));

        // Обнуляем position во всех DepartmentPosition
        departmentPositionRepository.nullifyPosition(id);

        // Удаляем саму должность
        positionRepository.delete(position);
    }

    @Transactional(readOnly = true)
    public List<PositionResponseDto> getAllPositions() {
        return positionRepository.findAll().stream()
                .map(PositionMapper::toResponseDto)
                .toList();
    }

    @Override
    public PositionResponseDto getPosition(Long id) {
        return positionRepository.findById(id)
                .map(PositionMapper::toResponseDto)
                .orElseThrow(() -> new PositionNotFoundException(id));
    }

    @Override
    public List<PositionResponseDto> getAllPositionsByDepartamentId(Long departamentId) {
        List<Long> positionsDepartment = departmentPositionRepository.findPositionIdsByDepartmentId(departamentId);

        return positionRepository.findAllById(positionsDepartment).stream()
                .map(PositionMapper::toResponseDto)
                .toList();
    }
}
