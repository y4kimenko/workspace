package by.diplom.workspace.booking.repository;

import by.diplom.workspace.booking.model.workplace.WorkplaceBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkplaceBookingRepository extends JpaRepository<WorkplaceBooking, UUID> {

    /**
     * Находит CONFIRMED-брони, у которых время начала уже наступило.
     */
    @Query("""
            SELECT wb FROM WorkplaceBooking wb
            WHERE wb.status = 'CONFIRMED'
              AND wb.startAt <= :now
            """)
    List<WorkplaceBooking> findConfirmedToStart(@Param("now") Instant now);

    /**
     * Находит IN_PROGRESS-брони, у которых время окончания уже наступило.
     */
    @Query("""
            SELECT wb FROM WorkplaceBooking wb
            WHERE wb.status = 'IN_PROGRESS'
              AND wb.endAt <= :now
            """)
    List<WorkplaceBooking> findInProgressToComplete(@Param("now") Instant now);

    /**
     * Все брони конкретного пользователя (без отменённых).
     */
    @Query("""
            SELECT wb FROM WorkplaceBooking wb
            JOIN FETCH wb.workplace w
            WHERE wb.createdBy.id = :userId
            ORDER BY wb.startAt DESC
            """)
    List<WorkplaceBooking> findAllByUserId(@Param("userId") UUID userId);

    /**
     * Поиск брони по id с проверкой владельца (для операций отмены/обновления).
     */
    @Query("""
            SELECT wb FROM WorkplaceBooking wb
            JOIN FETCH wb.workplace w
            WHERE wb.id = :bookingId
              AND wb.createdBy.id = :userId
            """)
    Optional<WorkplaceBooking> findByIdAndUserId(
            @Param("bookingId") UUID bookingId,
            @Param("userId") UUID userId
    );

    /**
     * Проверяет, есть ли пересекающиеся активные брони для рабочего места
     * в указанный диапазон времени (исключая саму бронь при обновлении).
     * <p>
     * Перекрытие: existingStart < newEnd AND existingEnd > newStart
     */
    @Query("""
            SELECT COUNT(wb) > 0 FROM WorkplaceBooking wb
            WHERE wb.workplace.id = :workplaceId
              AND wb.status IN ('CONFIRMED', 'IN_PROGRESS')
              AND wb.startAt < :endAt
              AND wb.endAt > :startAt
              AND (:excludeId IS NULL OR wb.id <> :excludeId)
            """)
    boolean existsOverlap(
            @Param("workplaceId") Long workplaceId,
            @Param("startAt") Instant startAt,
            @Param("endAt") Instant endAt,
            @Param("excludeId") UUID excludeId
    );

    /**
     * Возвращает все активные брони рабочего места за указанный день
     * (от dayStart включительно до dayEnd не включая),
     * исключая бронь с excludeId (для редактирования).
     */
    @Query("""
            SELECT wb FROM WorkplaceBooking wb
            WHERE wb.workplace.id = :workplaceId
              AND wb.status IN ('CONFIRMED', 'IN_PROGRESS')
              AND wb.startAt < :dayEnd
              AND wb.endAt > :dayStart
              AND (:excludeId IS NULL OR wb.id <> :excludeId)
            ORDER BY wb.startAt
            """)
    List<WorkplaceBooking> findActiveForDay(
            @Param("workplaceId") Long workplaceId,
            @Param("dayStart") Instant dayStart,
            @Param("dayEnd") Instant dayEnd,
            @Param("excludeId") UUID excludeId
    );
}