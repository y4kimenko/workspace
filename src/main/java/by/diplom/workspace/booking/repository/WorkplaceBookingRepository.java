package by.diplom.workspace.booking.repository;

import by.diplom.workspace.booking.model.workplace.WorkplaceBooking;
import by.diplom.workspace.booking.model.workplace.WorkplaceBookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface WorkplaceBookingRepository extends JpaRepository<WorkplaceBooking, UUID> {

    // Проверка пересечений: ищем активные брони на рабочее место в период
    @Query("""
                SELECT COUNT(b) > 0 FROM WorkplaceBooking b
                WHERE b.workplace.id = :workplaceId
                  AND b.status = 'CONFIRMED'
                  AND b.startAt < :endAt
                  AND b.endAt > :startAt
            """)
    boolean existsConflict(
            @Param("workplaceId") Long workplaceId,
            @Param("startAt") Instant startAt,
            @Param("endAt") Instant endAt
    );

    // То же, но исключая конкретную бронь (для обновления)
    @Query("""
                SELECT COUNT(b) > 0 FROM WorkplaceBooking b
                WHERE b.workplace.id = :workplaceId
                  AND b.status = 'CONFIRMED'
                  AND b.id <> :excludeId
                  AND b.startAt < :endAt
                  AND b.endAt > :startAt
            """)
    boolean existsConflictExcluding(
            @Param("workplaceId") Long workplaceId,
            @Param("startAt") Instant startAt,
            @Param("endAt") Instant endAt,
            @Param("excludeId") UUID excludeId
    );

    List<WorkplaceBooking> findByCreatedByIdOrderByStartAtDesc(UUID userId);

    List<WorkplaceBooking> findByWorkplaceIdOrderByStartAtAsc(Long workplaceId);

    @Query("""
                SELECT b FROM WorkplaceBooking b
                WHERE b.workplace.id = :workplaceId
                  AND b.status = :status
                  AND b.startAt >= :from
                  AND b.endAt <= :to
                ORDER BY b.startAt
            """)
    List<WorkplaceBooking> findByWorkplaceAndStatusAndPeriod(
            @Param("workplaceId") Long workplaceId,
            @Param("status") WorkplaceBookingStatus status,
            @Param("from") Instant from,
            @Param("to") Instant to
    );
}