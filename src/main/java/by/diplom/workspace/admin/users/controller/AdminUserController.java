package by.diplom.workspace.admin.users.controller;

import by.diplom.workspace.admin.users.dto.request.CreateUserRequestDto;
import by.diplom.workspace.admin.users.dto.request.UserUpdateRequestDto;
import by.diplom.workspace.admin.users.dto.response.CreateUserResponseDto;
import by.diplom.workspace.admin.users.dto.response.UserResponseDto;
import by.diplom.workspace.admin.users.service.AdminUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

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

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUser(@PathVariable UUID id) {
        adminUserService.deleteUser(id);
    }


    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDto updateUser(@PathVariable UUID id,
                           @Valid @RequestBody UserUpdateRequestDto request) {
        return adminUserService.update(id, request);
    }

    @GetMapping
    public List<UserResponseDto> getAllUsers() {
        return adminUserService.getAllUser();
    }
}
