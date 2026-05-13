package by.diplom.workspace.worker.avatar.controller;


import by.diplom.workspace.security.AppUserDetails;
import by.diplom.workspace.worker.avatar.dto.UserAvatarResponse;
import by.diplom.workspace.worker.avatar.service.UserAvatarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserAvatarController {

    private final UserAvatarService userAvatarService;

    /**
     * Получить текущий путь к аватару пользователя.
     * Доступно всем аутентифицированным пользователям.
     */
    @GetMapping("/{userId}/avatar")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.OK)
    public UserAvatarResponse getAvatar(@PathVariable UUID userId) {
        return userAvatarService.getAvatar(userId);
    }

    /**
     * Загрузить / заменить аватар.
     * Только сам пользователь может менять свой аватар.
     */
    @PostMapping("/{userId}/avatar")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'GROUP_MANAGER')")
    @ResponseStatus(HttpStatus.OK)
    public UserAvatarResponse uploadAvatar(
            @PathVariable UUID userId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal AppUserDetails currentUser
    ) {

        return userAvatarService.uploadAvatar(userId, currentUser.getId(), file);
    }

    /**
     * Удалить аватар.
     * Только сам пользователь может удалить свой аватар.
     */
    @DeleteMapping("/{userId}/avatar")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'GROUP_MANAGER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAvatar(
            @PathVariable UUID userId,
            @AuthenticationPrincipal AppUserDetails currentUser
    ) {
        userAvatarService.deleteAvatar(currentUser.getId(), userId);
    }

    /**
     * Пользователь может управлять только своим аватаром.
     */
    private void checkOwnership(UUID targetUserId, AppUserDetails currentUser) {
        if (!currentUser.getId().equals(targetUserId)) {
            throw new AccessDeniedException("Нельзя изменять аватар другого пользователя");
        }
    }
}