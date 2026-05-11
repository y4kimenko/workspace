package by.diplom.workspace.worker.avatar.service;

import by.diplom.workspace.worker.avatar.dto.UserAvatarResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface UserAvatarService {
    UserAvatarResponse uploadAvatar(UUID reqUserId, UUID userId, MultipartFile file);

    void deleteAvatar(UUID reqUserId, UUID userId);

    UserAvatarResponse getAvatar(UUID userId);
}

