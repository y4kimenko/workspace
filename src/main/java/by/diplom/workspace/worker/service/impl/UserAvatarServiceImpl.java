package by.diplom.workspace.worker.service.impl;


import by.diplom.workspace.worker.model.user.User;
import by.diplom.workspace.worker.dto.UserAvatarResponse;
import by.diplom.workspace.worker.repository.UserRepository;
import by.diplom.workspace.worker.service.UserAvatarService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserAvatarServiceImpl implements UserAvatarService {

    private static final String AVATAR_DIRECTORY = "uploads/avatars";

    private final UserRepository userRepository;

    @Transactional
    public UserAvatarResponse uploadAvatar(UUID userId, MultipartFile file) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        validateFile(file);

        deleteOldAvatar(user.getAvatarPath());

        String fileName = generateFileName(file);
        Path uploadPath = Paths.get(AVATAR_DIRECTORY);

        try {
            Files.createDirectories(uploadPath);

            Path filePath = uploadPath.resolve(fileName);

            Files.copy(
                    file.getInputStream(),
                    filePath,
                    StandardCopyOption.REPLACE_EXISTING
            );

            String avatarPath = "/uploads/avatars/" + fileName;

            user.setAvatarPath(avatarPath);
            userRepository.save(user);

            return new UserAvatarResponse(avatarPath);
        } catch (IOException exception) {
            throw new RuntimeException("Ошибка при сохранении аватарки", exception);
        }
    }

    @Transactional
    public void deleteAvatar(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        deleteOldAvatar(user.getAvatarPath());

        user.setAvatarPath(null);
        userRepository.save(user);
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Файл не должен быть пустым");
        }

        String contentType = file.getContentType();

        if (!List.of("image/jpeg", "image/png", "image/webp").contains(contentType)) {
            throw new RuntimeException("Можно загружать только jpeg, png или webp");
        }
    }

    private String generateFileName(MultipartFile file) {
        String originalFileName = file.getOriginalFilename();

        String extension = "";

        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }

        return UUID.randomUUID() + extension;
    }

    private void deleteOldAvatar(String avatarPath) {
        if (avatarPath == null || avatarPath.isBlank()) {
            return;
        }

        String fileName = Paths.get(avatarPath).getFileName().toString();
        Path oldFilePath = Paths.get(AVATAR_DIRECTORY).resolve(fileName);

        try {
            Files.deleteIfExists(oldFilePath);
        } catch (IOException exception) {
            throw new RuntimeException("Ошибка при удалении старой аватарки", exception);
        }
    }
}
