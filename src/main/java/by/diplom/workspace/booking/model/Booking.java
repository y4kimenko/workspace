package by.diplom.workspace.booking.model;


import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;


@Getter
@MappedSuperclass
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "start_at", nullable = false)
    private Instant startAt;

    @Column(name = "end_at", nullable = false)
    private Instant endAt;

    protected Booking(
            LocalDateTime startAt,
            LocalDateTime endAt,
            ZoneId zoneId
    ) {
        validateDateTime(startAt, endAt);

        if (zoneId == null) {
            throw new IllegalArgumentException("Таймзона бронирования обязательна");
        }

        this.startAt = startAt.atZone(zoneId).toInstant();
        this.endAt = endAt.atZone(zoneId).toInstant();
    }


    public Duration getDuration() {
        return Duration.between(startAt, endAt);
    }

    protected void changePeriod(
            LocalDateTime newStartAt,
            LocalDateTime newEndAt,
            ZoneId zoneId
    ) {
        validateDateTime(newStartAt, newEndAt);

        if (zoneId == null) {
            throw new IllegalArgumentException("Таймзона бронирования обязательна");
        }

        this.startAt = newStartAt.atZone(zoneId).toInstant();
        this.endAt = newEndAt.atZone(zoneId).toInstant();
    }

    private static void validateDateTime(
            LocalDateTime startAt,
            LocalDateTime endAt
    ) {
        if (startAt == null || endAt == null) {
            throw new IllegalArgumentException("Дата начала и дата окончания бронирования обязательны");
        }

        if (!endAt.isAfter(startAt)) {
            throw new IllegalArgumentException("Дата окончания должна быть позже даты начала");
        }

        validateTimeSlot(startAt);
        validateTimeSlot(endAt);

    }

    private static void validateTimeSlot(LocalDateTime dateTime) {
        if (dateTime.getMinute() % 15 != 0
                || dateTime.getSecond() != 0
                || dateTime.getNano() != 0) {
            throw new IllegalArgumentException(
                    "Время бронирования должно быть кратно 15 минутам (например, 12:00, 12:15, 12:30, 12:45)"
            );
        }
    }

    private static void validateInstantPeriod(
            Instant startAt,
            Instant endAt
    ) {
        if (startAt == null || endAt == null) {
            throw new IllegalArgumentException("Дата начала и дата окончания обязательны");
        }

        if (!endAt.isAfter(startAt)) {
            throw new IllegalArgumentException("Дата окончания должна быть позже даты начала");
        }

        validateTimeSlot(startAt.atZone(ZoneId.systemDefault()).toLocalDateTime());
        validateTimeSlot(endAt.atZone(ZoneId.systemDefault()).toLocalDateTime());
    }

}