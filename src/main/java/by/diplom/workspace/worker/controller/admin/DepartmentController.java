package by.diplom.workspace.worker.controller.admin;

import by.diplom.workspace.worker.service.admin.inter.position.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class DepartmentController {
    private final DepartmentService departmentService;
}
