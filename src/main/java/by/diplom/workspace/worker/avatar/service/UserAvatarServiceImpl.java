package by.diplom.workspace.worker.avatar.service;

import by.diplom.workspace.worker.avatar.AvatarStorageProperties;
import by.diplom.workspace.worker.avatar.dto.UserAvatarResponse;
import by.diplom.workspace.worker.avatar.exception.AvatarException;
import by.diplom.workspace.worker.worker.model.user.User;
import by.diplom.workspace.worker.worker.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserAvatarServiceImpl implements UserAvatarService {

    // Определяем допустимые MIME-типы и соответствующие им расширения.
    // Расширение выводится из content-type, а НЕ из имени файла присланного клиентом,
    // чтобы исключить подмену (например, evil.php → image/jpeg).
    private static final Map<String, String> ALLOWED_TYPES = Map.of(
            "image/jpeg", ".jpg",
            "image/png", ".png",
            "image/webp", ".webp"
    );

    private final UserRepository userRepository;
    private final AvatarStorageProperties storageProperties;

    @Override
    @Transactional(readOnly = true)
    public UserAvatarResponse getAvatar(UUID userId) {
        User user = findUserOrThrow(userId);
        return new UserAvatarResponse(user.getAvatarPath());
    }

    @Override
    @Transactional
    public UserAvatarResponse uploadAvatar(UUID userId, UUID userAvatarId, MultipartFile file) {
        if (!userId.equals(userAvatarId))
            throw new AvatarException("Данный пользователь не имеет таких прав");

        User user = findUserOrThrow(userAvatarId);

        validateFile(file);

        // Удаляем старый файл до сохранения нового, чтобы не накапливать мусор
        deletePhysicalFile(user.getAvatarPath());

        String fileName = generateSafeFileName(file);
        Path uploadDir = Paths.get(storageProperties.getStoragePath());

        try {
            Files.createDirectories(uploadDir);
            Files.copy(file.getInputStream(), uploadDir.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new AvatarException("Не удалось сохранить файл аватара", e);
        }

        String avatarPath = "/uploads/avatars/" + fileName;
        user.setAvatarPath(avatarPath);
        userRepository.save(user);

        return new UserAvatarResponse(avatarPath);
    }

    @Override
    @Transactional
    public void deleteAvatar(UUID userId, UUID userAvatarId) {
        if (!userId.equals(userAvatarId))
            throw new AvatarException("Данный пользователь не имеет таких прав");

        User user = findUserOrThrow(userAvatarId);

        if (user.getAvatarPath() == null) {
            // Аватара нет — ничего не делаем, не бросаем ошибку
            return;
        }

        deletePhysicalFile(user.getAvatarPath());
        user.setAvatarPath(null);
        userRepository.save(user);
    }

    // --- private helpers ---

    private User findUserOrThrow(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден: " + userId));
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new AvatarException("Файл не может быть пустым");
        }

        if (file.getSize() > storageProperties.getMaxFileSizeBytes()) {
            long limitMb = storageProperties.getMaxFileSizeBytes() / (1024 * 1024);
            throw new AvatarException("Размер файла превышает допустимый лимит (%d МБ)".formatted(limitMb));
        }

        if (!ALLOWED_TYPES.containsKey(file.getContentType())) {
            throw new AvatarException("Допустимые форматы: JPEG, PNG, WebP");
        }
    }

    /**
     * Генерирует безопасное имя файла: UUID + расширение из content-type,
     * игнорируя оригинальное имя, пришедшее от клиента.
     */
    private String generateSafeFileName(MultipartFile file) {
        String extension = ALLOWED_TYPES.get(file.getContentType());
        return UUID.randomUUID() + extension;
    }

    private void deletePhysicalFile(String avatarPath) {
        if (avatarPath == null || avatarPath.isBlank()) {
            return;
        }

        // Берём только имя файла, чтобы не допустить path-traversal атаку
        // (вдруг в БД окажется что-то вроде "../../etc/passwd")
        String fileName = Paths.get(avatarPath).getFileName().toString();
        Path filePath = Paths.get(storageProperties.getStoragePath()).resolve(fileName);

        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new AvatarException("Не удалось удалить старый файл аватара", e);
        }
    }
}