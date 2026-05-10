package by.diplom.workspace.position.controller;

import by.diplom.workspace.position.dto.request.create.CreateDepartmentRequestDto;
import by.diplom.workspace.position.dto.request.update.UpdateDepartmentRequestDto;
import by.diplom.workspace.position.dto.response.DepartmentResponseDto;
import by.diplom.workspace.position.service.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/departments")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class DepartamentController {
    private final DepartmentService departmentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DepartmentResponseDto createDepartment(
            @Valid @RequestBody CreateDepartmentRequestDto request) {
        return departmentService.createDepartment(request);
    }

    @GetMapping
    public List<DepartmentResponseDto> getAllDepartments() {
        return departmentService.getAllDepartments();
    }

    @GetMapping("/{id}")
    public DepartmentResponseDto getDepartment(@PathVariable Long id) {
        return departmentService.getDepartment(id);
    }

    @PutMapping("/{id}")
    public DepartmentResponseDto updateDepartment(
            @PathVariable Long id,
            @Valid @RequestBody UpdateDepartmentRequestDto request) {
        return departmentService.updateDepartment(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
    }
}
