package by.diplom.workspace.avatar;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.avatar")
public class AvatarStorageProperties {

    /**
     * Абсолютный путь к директории хранения аватаров.
     * Пример в application.yml:
     * app.avatar.storage-path: /var/app/uploads/avatars
     */
    private String storagePath = "uploads/avatars";

    /**
     * Максимальный размер файла в байтах. По умолчанию 5 МБ.
     */
    private long maxFileSizeBytes = 5 * 1024 * 1024L;
}
