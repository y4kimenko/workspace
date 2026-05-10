package by.diplom.workspace.worker.service.admin.inter.position;

import by.diplom.workspace.worker.dto.position.request.create.CreateDepartmentRequestDto;
import by.diplom.workspace.worker.dto.position.request.update.UpdateDepartmentRequestDto;
import by.diplom.workspace.worker.dto.position.response.DepartmentResponseDto;

import java.util.List;

public interface DepartmentService {
    DepartmentResponseDto createDepartment(CreateDepartmentRequestDto request);
    DepartmentResponseDto updateDepartment(Long id, UpdateDepartmentRequestDto request);
    void deleteDepartment(Long id);
    List<DepartmentResponseDto> getAllDepartments();
    DepartmentResponseDto getDepartment(Long id);
}
