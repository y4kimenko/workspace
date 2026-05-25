package by.diplom.workspace.admin.request_registration.service.impl;

import by.diplom.workspace.admin.request_registration.model.StatusRegistration;
import by.diplom.workspace.admin.request_registration.repository.RegistrationRequestRepository;
import by.diplom.workspace.worker.email.repository.EmailVerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Service
@RequiredArgsConstructor
public class RegistrationExpiryService {

    private static final long TTL_MINUTES = 15;

    private final TaskScheduler taskScheduler;
    private final PlatformTransactionManager transactionManager;
    private final RegistrationRequestRepository registrationRequestRepository;
    private final EmailVerificationTokenRepository tokenRepository;

    private final Map<Long, ScheduledFuture<?>> expiryTasks = new ConcurrentHashMap<>();

    public void scheduleExpiry(Long registrationRequestId) {
        cancelExpiry(registrationRequestId);

        ScheduledFuture<?> future = taskScheduler.schedule(
                () -> expireIfEmailNotVerified(registrationRequestId),
                Instant.now().plusSeconds(TTL_MINUTES * 60)
        );

        expiryTasks.put(registrationRequestId, future);
    }

    public void cancelExpiry(Long registrationRequestId) {
        ScheduledFuture<?> future = expiryTasks.remove(registrationRequestId);

        if (future != null) {
            future.cancel(false);
        }
    }

    private void expireIfEmailNotVerified(Long registrationRequestId) {
        try {
            TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);

            transactionTemplate.executeWithoutResult(status ->
                    registrationRequestRepository.findById(registrationRequestId)
                            .ifPresent(registrationRequest -> {
                                // Если email так и не был подтверждён, удаляем заявку и токен
                                if (!registrationRequest.isEmailIsVerified()
                                        && registrationRequest.getStatus() == StatusRegistration.WAITING) {
                                    tokenRepository.deleteByEmail(registrationRequest.getEmail());
                                    registrationRequestRepository.delete(registrationRequest);
                                }
                            })
            );
        } finally {
            expiryTasks.remove(registrationRequestId);
        }
    }
}
