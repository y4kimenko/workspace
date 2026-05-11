package by.diplom.workspace.avatar.service;

import by.diplom.workspace.avatar.dto.UserAvatarResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface UserAvatarService {
    UserAvatarResponse uploadAvatar(UUID reqUserId, UUID userId, MultipartFile file);

    void deleteAvatar(UUID userId);

    UserAvatarResponse getAvatar(UUID userId);
}

