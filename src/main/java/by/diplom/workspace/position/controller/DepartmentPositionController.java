package by.diplom.workspace.position.controller;

import by.diplom.workspace.position.dto.request.create.CreateDepartmentPositionRequestDto;
import by.diplom.workspace.position.dto.response.DepartmentPositionResponseDto;
import by.diplom.workspace.position.service.DepartmentPositionService;
import by.diplom.workspace.position.dto.request.update.UpdateDepartmentPositionRequestDto;
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
@RequestMapping("/api/admin/department-positions")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class DepartmentPositionController {

    private final DepartmentPositionService departmentPositionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DepartmentPositionResponseDto create(
            @RequestBody @Valid CreateDepartmentPositionRequestDto request) {
        return departmentPositionService.create(request);
    }

    @GetMapping
    public List<DepartmentPositionResponseDto> getAll() {
        return departmentPositionService.getAll();
    }

    @GetMapping("/{id}")
    public DepartmentPositionResponseDto getById(@PathVariable Long id) {
        return departmentPositionService.getById(id);
    }

    @GetMapping("/by-department/{departmentId}")
    public List<DepartmentPositionResponseDto> getByDepartment(
            @PathVariable Long departmentId) {
        return departmentPositionService.getByDepartment(departmentId);
    }

    @GetMapping("/by-position/{positionId}")
    public List<DepartmentPositionResponseDto> getByPosition(
            @PathVariable Long positionId) {
        return departmentPositionService.getByPosition(positionId);
    }

    @PutMapping("/{id}")
    public DepartmentPositionResponseDto update(
            @PathVariable Long id,
            @RequestBody UpdateDepartmentPositionRequestDto request) {
        return departmentPositionService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        departmentPositionService.delete(id);
    }
}
