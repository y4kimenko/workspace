package by.diplom.workspace.booking.model;

import by.diplom.workspace.shared.time.TimeZoneAware;
import by.diplom.workspace.shared.time.TimeZoneSupport;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Id;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Getter
@MappedSuperclass
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Booking implements TimeZoneAware {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "start_at", nullable = false)
    private Instant startAt;

    @Column(name = "end_at", nullable = false)
    private Instant endAt;

    @Column(name = "time_zone", nullable = false, length = 64)
    private String timeZone = TimeZoneSupport.DEFAULT_TIME_ZONE;


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
        this.timeZone = zoneId.getId();
    }

    public LocalDateTime getStartAtInBookingTimeZone() {
        return LocalDateTime.ofInstant(startAt, getZoneId());
    }

    public LocalDateTime getEndAtInBookingTimeZone() {
        return LocalDateTime.ofInstant(endAt, getZoneId());
    }

    public Duration getDuration() {
        return Duration.between(startAt, endAt);
    }

    public boolean overlapsWith(LocalDateTime otherStartAt, LocalDateTime otherEndAt) {
        validateDateTime(otherStartAt, otherEndAt);

        ZoneId zoneId = getZoneId();

        Instant otherStartInstant = otherStartAt.atZone(zoneId).toInstant();
        Instant otherEndInstant = otherEndAt.atZone(zoneId).toInstant();

        return this.startAt.isBefore(otherEndInstant)
                && this.endAt.isAfter(otherStartInstant);
    }

    public boolean overlapsWith(Instant otherStartAt, Instant otherEndAt) {
        validateInstantPeriod(otherStartAt, otherEndAt);

        return this.startAt.isBefore(otherEndAt)
                && this.endAt.isAfter(otherStartAt);
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
        this.timeZone = zoneId.getId();
    }

    protected void changePeriod(
            LocalDateTime newStartAt,
            LocalDateTime newEndAt,
            String newTimeZone
    ) {
        validateDateTime(newStartAt, newEndAt);

        ZoneId zoneId = TimeZoneSupport.toZoneId(newTimeZone);

        this.startAt = newStartAt.atZone(zoneId).toInstant();
        this.endAt = newEndAt.atZone(zoneId).toInstant();
        this.timeZone = zoneId.getId();
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
    }

}