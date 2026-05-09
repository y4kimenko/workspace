package by.diplom.workspace.worker.controller;

import by.diplom.workspace.worker.dto.UserAvatarResponse;
import by.diplom.workspace.worker.service.UserAvatarService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserAvatarController {

    private final UserAvatarService userAvatarService;

    @PostMapping("/{userId}/avatar")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'GROUP_MANAGER')")
    public UserAvatarResponse uploadAvatar(
            @PathVariable UUID userId,
            @RequestParam("file") MultipartFile file
    ) {
        return userAvatarService.uploadAvatar(userId, file);
    }

    @DeleteMapping("/{userId}/avatar")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'GROUP_MANAGER')")
    public void deleteAvatar(@PathVariable UUID userId) {
        userAvatarService.deleteAvatar(userId);
    }
}