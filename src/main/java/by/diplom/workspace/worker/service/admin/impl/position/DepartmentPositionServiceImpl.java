package by.diplom.workspace.worker.service.admin.impl.position;

import by.diplom.workspace.worker.dto.position.request.create.CreateDepartmentPositionRequestDto;
import by.diplom.workspace.worker.dto.position.request.update.UpdateDepartmentPositionRequestDto;
import by.diplom.workspace.worker.dto.position.response.DepartmentPositionResponseDto;
import by.diplom.workspace.worker.exception.position.DepartmentNotFoundException;
import by.diplom.workspace.worker.exception.position.DepartmentPositionAlreadyExistsException;
import by.diplom.workspace.worker.exception.position.DepartmentPositionNotFoundException;
import by.diplom.workspace.worker.exception.position.PositionNotFoundException;
import by.diplom.workspace.worker.mapper.position.DepartmentPositionMapper;
import by.diplom.workspace.worker.model.user.profile.position.Department;
import by.diplom.workspace.worker.model.user.profile.position.DepartmentPosition;
import by.diplom.workspace.worker.repository.UserRepository;
import by.diplom.workspace.worker.repository.position.DepartmentPositionRepository;
import by.diplom.workspace.worker.repository.position.DepartmentRepository;
import by.diplom.workspace.worker.repository.position.PositionRepository;
import by.diplom.workspace.worker.service.admin.inter.position.DepartmentPositionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import by.diplom.workspace.worker.model.user.profile.position.Position;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentPositionServiceImpl implements DepartmentPositionService {

    private final DepartmentPositionRepository departmentPositionRepository;
    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;
    private final UserRepository userRepository;

    @Transactional
    public DepartmentPositionResponseDto create(CreateDepartmentPositionRequestDto request) {
        Department department = resolveDepartment(request.departmentId());
        Position position = resolvePosition(request.positionId());

        if (departmentPositionRepository.existsByDepartmentIdAndPositionId(department.getId(), position.getId())) {
            throw new DepartmentPositionAlreadyExistsException();
        }

        DepartmentPosition dp = new DepartmentPosition(department, position);
        departmentPositionRepository.save(dp);
        return DepartmentPositionMapper.toResponseDto(dp);
    }

    @Transactional
    public DepartmentPositionResponseDto update(Long id, UpdateDepartmentPositionRequestDto request) {
        DepartmentPosition dp = departmentPositionRepository.findById(id)
                .orElseThrow(() -> new DepartmentPositionNotFoundException(id));

        Department department = resolveDepartment(request.departmentId());
        Position position = resolvePosition(request.positionId());

        // Проверяем дубликат только если оба поля не null
        if (department != null && position != null
                && departmentPositionRepository.existsByDepartmentIdAndPositionIdAndIdNot(
                department.getId(), position.getId(), id)) {
            throw new DepartmentPositionAlreadyExistsException();
        }

        dp.setDepartment(department);
        dp.setPosition(position);
        return DepartmentPositionMapper.toResponseDto(dp);
    }

    @Transactional
    public void delete(Long id) {
        DepartmentPosition dp = departmentPositionRepository.findById(id)
                .orElseThrow(() -> new DepartmentPositionNotFoundException(id));

        // Обнуляем departmentPosition у всех пользователей с этой связкой
        userRepository.clearDepartmentPositionByDepartmentPositionId(id);

        departmentPositionRepository.delete(dp);
    }

    @Transactional(readOnly = true)
    public DepartmentPositionResponseDto getById(Long id) {
        return departmentPositionRepository.findById(id)
                .map(DepartmentPositionMapper::toResponseDto)
                .orElseThrow(() -> new DepartmentPositionNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<DepartmentPositionResponseDto> getAll() {
        return departmentPositionRepository.findAll().stream()
                .map(DepartmentPositionMapper::toResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DepartmentPositionResponseDto> getByDepartment(Long departmentId) {
        if (!departmentRepository.existsById(departmentId)) {
            throw new DepartmentNotFoundException(departmentId);
        }
        return departmentPositionRepository.findAllByDepartmentId(departmentId).stream()
                .map(DepartmentPositionMapper::toResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DepartmentPositionResponseDto> getByPosition(Long positionId) {
        if (!positionRepository.existsById(positionId)) {
            throw new PositionNotFoundException(positionId);
        }
        return departmentPositionRepository.findAllByPositionId(positionId).stream()
                .map(DepartmentPositionMapper::toResponseDto)
                .toList();
    }

    // Хелперы ───────────────────────────────────────────────

    // Если id передан — ищем сущность, если null — возвращаем null (не назначено)
    private Department resolveDepartment(Long departmentId) {
        if (departmentId == null) return null;
        return departmentRepository.findById(departmentId)
                .orElseThrow(() -> new DepartmentNotFoundException(departmentId));
    }

    private Position resolvePosition(Long positionId) {
        if (positionId == null) return null;
        return positionRepository.findById(positionId)
                .orElseThrow(() -> new PositionNotFoundException(positionId));
    }
}