package by.diplom.workspace.worker.service;

import by.diplom.workspace.worker.dto.UserAvatarResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface UserAvatarService {
    UserAvatarResponse uploadAvatar(UUID userId, MultipartFile file);
    void deleteAvatar(UUID userId);
}
