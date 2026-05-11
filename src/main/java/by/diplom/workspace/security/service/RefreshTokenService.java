package by.diplom.workspace.security.service;

import by.diplom.workspace.security.RefreshToken;
import by.diplom.workspace.security.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${app.jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    @Transactional
    public RefreshToken createForUser(UUID userId) {
        refreshTokenRepository.revokeAllByUserId(userId);

        RefreshToken token = new RefreshToken();
        token.setToken(UUID.randomUUID().toString());
        token.setUserId(userId);
        token.setExpiresAt(Instant.now().plusMillis(refreshExpirationMs));
        return refreshTokenRepository.save(token);
    }

    @Transactional
    public RefreshToken createForAdmin(String adminUsername) {
        refreshTokenRepository.revokeAllByAdminUsername(adminUsername);

        RefreshToken token = new RefreshToken();
        token.setToken(UUID.randomUUID().toString());
        token.setAdminUsername(adminUsername);
        token.setExpiresAt(Instant.now().plusMillis(refreshExpirationMs));
        return refreshTokenRepository.save(token);
    }

    /**
     * Валидирует токен и сразу отзывает его (одноразовое использование — rotation).
     * Возвращает токен, если он валиден.
     *
     * @throws IllegalArgumentException если токен не найден, отозван или истёк
     */
    @Transactional
    public RefreshToken rotateToken(String rawToken) {
        RefreshToken stored = refreshTokenRepository.findByToken(rawToken)
                .orElseThrow(() -> new IllegalArgumentException("Refresh token not found"));

        if (stored.isRevoked()) {
            // Возможная атака повторного использования — отзываем ВСЕ токены этого субъекта
            if (stored.getUserId() != null) {
                refreshTokenRepository.revokeAllByUserId(stored.getUserId());
            } else {
                refreshTokenRepository.revokeAllByAdminUsername(stored.getAdminUsername());
            }
            throw new IllegalArgumentException("Refresh token already revoked (possible reuse attack)");
        }

        if (stored.getExpiresAt().isBefore(Instant.now())) {
            stored.setRevoked(true);
            refreshTokenRepository.save(stored);
            throw new IllegalArgumentException("Refresh token expired");
        }

        // Отзываем использованный токен (rotation)
        stored.setRevoked(true);
        refreshTokenRepository.save(stored);

        return stored;
    }
}
