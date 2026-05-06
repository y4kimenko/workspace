package by.diplom.workspace.worker.controller;


import by.diplom.workspace.worker.dto.UserAvatarResponse;
import by.diplom.workspace.worker.service.UserAvatarService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserAvatarController {


    private final UserAvatarService userAvatarService;

    @PostMapping("/{userId}/avatar")
    public UserAvatarResponse uploadAvatar(
            @PathVariable UUID userId,
            @RequestParam("file") MultipartFile file
    ) {
        return userAvatarService.uploadAvatar(userId, file);
    }

    @DeleteMapping("/{userId}/avatar")
    public void deleteAvatar(@PathVariable UUID userId) {
        userAvatarService.deleteAvatar(userId);
    }
}