package by.diplom.workspace.booking.model.workplace;

import by.diplom.workspace.booking.model.Booking;
import by.diplom.workspace.place.model.Workplace;
import by.diplom.workspace.worker.model.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(
        name = "workplace_bookings",
        indexes = {
                @Index(name = "idx_workplace_booking_workplace_id", columnList = "workplace_id"),
                @Index(name = "idx_workplace_booking_created_by_id", columnList = "created_by_id"),
                @Index(name = "idx_workplace_booking_time", columnList = "start_at, end_at")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WorkplaceBooking extends Booking {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "workplace_id", nullable = false)
    private Workplace workplace;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by_id", nullable = false)
    private User createdBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private WorkplaceBookingStatus status;

    public WorkplaceBooking(
            Workplace workplace,
            User createdBy,
            LocalDateTime startAt,
            LocalDateTime endAt
    ) {
        super(startAt, endAt, createdBy.getZoneId());
        this.workplace = workplace;
        this.createdBy = createdBy;
        this.status = WorkplaceBookingStatus.CONFIRMED;
    }

    public void cancel() {
        if (this.status == WorkplaceBookingStatus.COMPLETED) {
            throw new IllegalStateException("Нельзя отменить завершённое бронирование рабочего места");
        }

        this.status = WorkplaceBookingStatus.CANCELLED;
    }

    public void complete() {
        if (this.status == WorkplaceBookingStatus.CANCELLED) {
            throw new IllegalStateException("Нельзя завершить отменённое бронирование рабочего места");
        }

        this.status = WorkplaceBookingStatus.COMPLETED;
    }
}