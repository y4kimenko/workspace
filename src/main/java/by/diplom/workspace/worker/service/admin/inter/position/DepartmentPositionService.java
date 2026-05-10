package by.diplom.workspace.worker.service.admin.inter.position;

import by.diplom.workspace.worker.dto.position.request.create.CreateDepartmentPositionRequestDto;
import by.diplom.workspace.worker.dto.position.request.update.UpdateDepartmentPositionRequestDto;
import by.diplom.workspace.worker.dto.position.response.DepartmentPositionResponseDto;

import java.util.List;

public interface DepartmentPositionService {
    DepartmentPositionResponseDto create(CreateDepartmentPositionRequestDto request);
    DepartmentPositionResponseDto update(Long id, UpdateDepartmentPositionRequestDto request);
    void delete(Long id);
    DepartmentPositionResponseDto getById(Long id);
    List<DepartmentPositionResponseDto> getAll();
    List<DepartmentPositionResponseDto> getByDepartment(Long departmentId);
    List<DepartmentPositionResponseDto> getByPosition(Long positionId);
}
