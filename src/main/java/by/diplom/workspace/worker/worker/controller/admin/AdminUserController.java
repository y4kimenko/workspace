package by.diplom.workspace.worker.worker.controller.admin;

import by.diplom.workspace.worker.worker.dto.user.request.CreateUserRequestDto;
import by.diplom.workspace.worker.worker.dto.user.response.CreateUserResponseDto;
import by.diplom.workspace.worker.worker.service.AdminUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final AdminUserService adminUserService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateUserResponseDto createUser(@Valid @RequestBody CreateUserRequestDto request) {
        return adminUserService.createUser(request);
    }
}
