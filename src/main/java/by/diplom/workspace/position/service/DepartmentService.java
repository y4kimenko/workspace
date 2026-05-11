package by.diplom.workspace.position.service;

import by.diplom.workspace.position.dto.request.create.CreateDepartmentRequestDto;
import by.diplom.workspace.position.dto.request.update.UpdateDepartmentRequestDto;
import by.diplom.workspace.position.dto.response.DepartmentResponseDto;

import java.util.List;

public interface DepartmentService {
    DepartmentResponseDto createDepartment(CreateDepartmentRequestDto request);

    DepartmentResponseDto updateDepartment(Long id, UpdateDepartmentRequestDto request);

    void deleteDepartment(Long id);

    List<DepartmentResponseDto> getAllDepartments();

    DepartmentResponseDto getDepartment(Long id);
}
