package by.diplom.workspace.booking.scheduler;

import by.diplom.workspace.booking.model.workplace.WorkplaceBooking;
import by.diplom.workspace.booking.repository.WorkplaceBookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * Планировщик автоматически переводит бронирования рабочих мест
 * между статусами в соответствии с их временными рамками:
 *
 * <pre>
 *   CONFIRMED ──(startAt наступил)──► IN_PROGRESS ──(endAt наступил)──► COMPLETED
 * </pre>
 *
 * Частота проверок задаётся через {@code app.booking.scheduler.cron}
 * (по умолчанию каждую минуту).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WorkplaceBookingStatusScheduler {

    private final WorkplaceBookingRepository bookingRepository;

    /**
     * Запускается по cron-выражению из конфигурации.
     * Каждый тик выполняет два прохода:
     * <ol>
     *   <li>CONFIRMED → IN_PROGRESS (время начала наступило)</li>
     *   <li>IN_PROGRESS → COMPLETED  (время окончания наступило)</li>
     * </ol>
     */
    @Scheduled(fixedDelayString = "${app.booking.scheduler.interval}")
    @Transactional
    public void updateStatuses() {
        Instant now = Instant.now();

        int started   = startConfirmedBookings(now);
        int completed = completeFinishedBookings(now);

        if (started > 0 || completed > 0) {
            log.info(
                    "[WorkplaceBookingScheduler] tick={} | started={} | completed={}",
                    now, started, completed
            );
        }
    }

    // ------------------------------------------------------------------ //
    //  Приватные методы                                                   //
    // ------------------------------------------------------------------ //

    /**
     * Переводит подтверждённые брони в статус IN_PROGRESS,
     * если время начала уже наступило.
     *
     * @return количество обновлённых записей
     */
    private int startConfirmedBookings(Instant now) {
        List<WorkplaceBooking> toStart = bookingRepository.findConfirmedToStart(now);

        for (WorkplaceBooking booking : toStart) {
            try {
                booking.start();
                log.debug("[WorkplaceBookingScheduler] CONFIRMED → IN_PROGRESS | bookingId={}", booking.getId());
            } catch (IllegalStateException e) {
                // Гарантийный guard: на случай race condition или некорректного состояния
                log.warn(
                        "[WorkplaceBookingScheduler] Не удалось начать бронирование | bookingId={} | reason={}",
                        booking.getId(), e.getMessage()
                );
            }
        }

        return toStart.size();
    }

    /**
     * Переводит активные брони в статус COMPLETED,
     * если время окончания уже наступило.
     *
     * @return количество обновлённых записей
     */
    private int completeFinishedBookings(Instant now) {
        List<WorkplaceBooking> toComplete = bookingRepository.findInProgressToComplete(now);

        for (WorkplaceBooking booking : toComplete) {
            try {
                booking.complete();
                log.debug("[WorkplaceBookingScheduler] IN_PROGRESS → COMPLETED | bookingId={}", booking.getId());
            } catch (IllegalStateException e) {
                log.warn(
                        "[WorkplaceBookingScheduler] Не удалось завершить бронирование | bookingId={} | reason={}",
                        booking.getId(), e.getMessage()
                );
            }
        }

        return toComplete.size();
    }
}