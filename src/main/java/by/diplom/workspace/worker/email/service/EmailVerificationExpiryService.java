package by.diplom.workspace.worker.email.service;

import by.diplom.workspace.worker.email.model.UserEmail;
import by.diplom.workspace.worker.email.repository.EmailVerificationTokenRepository;
import by.diplom.workspace.worker.email.repository.UserEmailRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

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
    private Duration expiryDuration;

    private final TaskScheduler taskScheduler;
    private final UserEmailRepository userEmailRepository;
    private final EmailVerificationTokenRepository tokenRepository;
    private final TransactionTemplate transactionTemplate;

    private final Map<UUID, ScheduledFuture<?>> pendingTasks = new ConcurrentHashMap<>();

    public void scheduleExpiry(UUID userEmailId, Instant expiresAt) {
        scheduleAt(userEmailId, expiresAt);
    }

    public void scheduleExpiry(UUID userEmailId) {
        scheduleAt(userEmailId, Instant.now().plus(expiryDuration));
    }

    public void cancelExpiry(UUID userEmailId) {
        ScheduledFuture<?> future = pendingTasks.remove(userEmailId);

        if (future != null) {
            future.cancel(false);
            log.debug("Задача удаления для почты [id={}] отменена", userEmailId);
        }
    }

    @PostConstruct
    public void rescheduleOnStartup() {
        List<UserEmail> pending = userEmailRepository.findAllUnverified();

        if (pending.isEmpty()) {
            log.info("Неверифицированных почт для восстановления задач не найдено");
            return;
        }

        Instant now = Instant.now();
        int rescheduled = 0;

        for (UserEmail userEmail : pending) {
            tokenRepository.findByEmail(userEmail.getEmail()).ifPresentOrElse(token -> {
                Instant expiresAt = token.getExpiresAt();

                if (expiresAt.isAfter(now)) {
                    scheduleAt(userEmail.getId(), expiresAt);
                } else {
                    scheduleAt(userEmail.getId(), now.plusSeconds(5));
                }
            }, () -> {
                scheduleAt(userEmail.getId(), now.plusSeconds(5));
            });

            rescheduled++;
        }

        log.info("Восстановлено {} задач удаления неверифицированных почт", rescheduled);
    }

    private void scheduleAt(UUID userEmailId, Instant runAt) {
        cancelExpiry(userEmailId);

        ScheduledFuture<?> future = taskScheduler.schedule(
                () -> deleteIfStillUnverified(userEmailId),
                runAt
        );

        pendingTasks.put(userEmailId, future);

        log.debug("Задача удаления почты [id={}] запланирована на {}", userEmailId, runAt);
    }

    private void deleteIfStillUnverified(UUID userEmailId) {
        pendingTasks.remove(userEmailId);

        transactionTemplate.executeWithoutResult(status -> {
            userEmailRepository.findById(userEmailId).ifPresent(userEmail -> {
                if (userEmail.isVerified()) {
                    log.debug("Почта [id={}] уже верифицирована – удаление пропущено", userEmailId);
                    return;
                }

                if (userEmail.isPrimaryEmail()) {
                    log.warn(
                            "Почта [id={}, email={}] является основной – удаление пропущено",
                            userEmailId,
                            userEmail.getEmail()
                    );
                    return;
                }

                tokenRepository.deleteByEmail(userEmail.getEmail());
                userEmailRepository.delete(userEmail);

                log.info(
                        "Неверифицированная почта [id={}, email={}] удалена – истёк срок подтверждения ({} мин)",
                        userEmailId,
                        userEmail.getEmail(),
                        expiryDuration.toMinutes()
                );
            });
        });
    }
}