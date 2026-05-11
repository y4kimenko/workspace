package by.diplom.workspace.email.service;

import by.diplom.workspace.email.model.UserEmail;
import by.diplom.workspace.email.repository.UserEmailRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailVerificationExpiryService {

    @Value("${app.email-verification.expiry-duration}")
    private Duration EXPIRY_DURATION;

    private final TaskScheduler taskScheduler;
    private final UserEmailRepository userEmailRepository;

    // Храним Future, чтобы иметь возможность отменить задачу при верификации
    // или ручном удалении почты до истечения срока
    private final Map<UUID, ScheduledFuture<?>> pendingTasks = new ConcurrentHashMap<>();

    // ── Планирование ──────────────────────────────────────────────────────────

    /**
     * Планирует удаление неверифицированной почты ровно через 15 минут.
     * Вызывается сразу после добавления нового email-адреса пользователю.
     *
     * @param userEmailId ID записи UserEmail
     */
    public void scheduleExpiry(UUID userEmailId) {
        scheduleAt(userEmailId, Instant.now().plus(EXPIRY_DURATION));
    }

    /**
     * Отменяет запланированное удаление.
     * Вызывается когда пользователь успешно верифицировал почту
     * или вручную удалил её раньше срока.
     *
     * @param userEmailId ID записи UserEmail
     */
    public void cancelExpiry(UUID userEmailId) {
        ScheduledFuture<?> future = pendingTasks.remove(userEmailId);
        if (future != null) {
            future.cancel(false); // false — не прерывать, если уже выполняется
            log.debug("Задача удаления для почты [id={}] отменена", userEmailId);
        }
    }

    // ── Восстановление задач при перезапуске ─────────────────────────────────

    /**
     * При старте приложения восстанавливает задачи для всех неверифицированных почт,
     * срок которых ещё не истёк. Закрывает кейс перезапуска приложения.
     */
    @PostConstruct
    public void rescheduleOnStartup() {
        List<UserEmail> pending = userEmailRepository.findAllUnverified();

        if (pending.isEmpty()) {
            log.info("Неверифицированных почт для восстановления задач не найдено");
            return;
        }

        Instant now = Instant.now();
        int rescheduled = 0;

        for (UserEmail email : pending) {
            Instant expiresAt = email.getCreatedAt().plus(EXPIRY_DURATION);

            if (expiresAt.isAfter(now)) {
                // Срок ещё не истёк — планируем на оставшееся время
                scheduleAt(email.getId(), expiresAt);
            } else {
                // Просрочена пока приложение было выключено — удаляем через 5 сек
                scheduleAt(email.getId(), now.plusSeconds(5));
            }
            rescheduled++;
        }

        log.info("Восстановлено {} задач удаления неверифицированных почт", rescheduled);
    }

    // ── Внутренние методы ─────────────────────────────────────────────────────

    private void scheduleAt(UUID userEmailId, Instant runAt) {
        ScheduledFuture<?> future = taskScheduler.schedule(
                () -> deleteIfStillUnverified(userEmailId),
                runAt
        );
        pendingTasks.put(userEmailId, future);
        log.debug("Задача удаления почты [id={}] запланирована на {}", userEmailId, runAt);
    }

    @Transactional
    protected void deleteIfStillUnverified(UUID userEmailId) {
        pendingTasks.remove(userEmailId); // чистим map после выполнения

        userEmailRepository.findById(userEmailId).ifPresent(email -> {
            if (!email.isVerified()) {
                userEmailRepository.delete(email);
                log.info(
                        "Неверифицированная почта [id={}, email={}] удалена — истёк срок подтверждения ({} мин)",
                        userEmailId, email.getEmail(), EXPIRY_DURATION.toMinutes()
                );
            } else {
                log.debug("Почта [id={}] уже верифицирована — удаление пропущено", userEmailId);
            }
        });
    }
}