package by.diplom.workspace.worker.service.admin.impl.position;

import by.diplom.workspace.worker.dto.position.request.create.CreateDepartmentRequestDto;
import by.diplom.workspace.worker.dto.position.request.update.UpdateDepartmentRequestDto;
import by.diplom.workspace.worker.dto.position.response.DepartmentResponseDto;
import by.diplom.workspace.worker.exception.position.DepartmentAlreadyExistsException;
import by.diplom.workspace.worker.exception.position.DepartmentNotFoundException;
import by.diplom.workspace.worker.mapper.position.DepartmentMapper;
import by.diplom.workspace.worker.model.user.profile.position.Department;
import by.diplom.workspace.worker.repository.UserRepository;
import by.diplom.workspace.worker.repository.position.DepartmentPositionRepository;
import by.diplom.workspace.worker.repository.position.DepartmentRepository;
import by.diplom.workspace.worker.service.admin.inter.position.DepartmentService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final DepartmentPositionRepository departmentPositionRepository;
    private final UserRepository userRepository;

    @Transactional
    public DepartmentResponseDto createDepartment(CreateDepartmentRequestDto request) {
        if (departmentRepository.existsByNameIgnoreCase(request.name())) {
            throw new DepartmentAlreadyExistsException(request.name());
        }

        Department department = new Department(request.name());
        departmentRepository.save(department);
        return DepartmentMapper.toResponseDto(department);
    }

    @Transactional
    public DepartmentResponseDto updateDepartment(Long id, UpdateDepartmentRequestDto request) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new DepartmentNotFoundException(id));

        // Проверяем существование другого отдела с таким именем (без учёта регистра)
        if (departmentRepository.existsByNameIgnoreCaseAndIdNot(request.name(), id)) {
            throw new DepartmentAlreadyExistsException(request.name());
        }

        department.setName(request.name());
        return DepartmentMapper.toResponseDto(department);
    }

    @Transactional
    public void deleteDepartment(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new DepartmentNotFoundException(id));

        // Обнуляем department во всех DepartmentPosition
        departmentPositionRepository.nullifyDepartment(id);

        // Удаляем сам отдел
        departmentRepository.delete(department);
    }

    @Transactional(readOnly = true)
    public List<DepartmentResponseDto> getAllDepartments() {
        return departmentRepository.findAll().stream()
                .map(DepartmentMapper::toResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public DepartmentResponseDto getDepartment(Long id) {
        return departmentRepository.findById(id)
                .map(DepartmentMapper::toResponseDto)
                .orElseThrow(() -> new DepartmentNotFoundException(id));
    }




}
