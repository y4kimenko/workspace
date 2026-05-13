package by.diplom.workspace.booking.model;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.UUID;

@Getter
@MappedSuperclass
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Booking {

    public static final LocalTime DAY_START = LocalTime.of(9, 0);
    public static final LocalTime DAY_END = LocalTime.of(23, 0);
    public static final int SLOT_MINUTES = 15;

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
        changePeriod(startAt, endAt, zoneId);
    }

    protected void changePeriod(
            LocalDateTime newStartAt,
            LocalDateTime newEndAt,
            ZoneId zoneId
    ) {
        validateZoneId(zoneId);
        validateDateTime(newStartAt, newEndAt);

        this.startAt = newStartAt.atZone(zoneId).toInstant();
        this.endAt = newEndAt.atZone(zoneId).toInstant();
    }

    private static void validateZoneId(ZoneId zoneId) {
        if (zoneId == null) {
            throw new IllegalArgumentException("Таймзона бронирования обязательна");
        }
    }

    private static void validateDateTime(
            LocalDateTime startAt,
            LocalDateTime endAt
    ) {
        if (startAt == null || endAt == null) {
            throw new IllegalArgumentException("Дата начала и дата окончания бронирования обязательны");
        }

        validateTimeSlot(startAt);
        validateTimeSlot(endAt);

        if (!endAt.isAfter(startAt)) {
            throw new IllegalArgumentException("Дата окончания должна быть позже даты начала");
        }

        if (!startAt.toLocalDate().equals(endAt.toLocalDate())) {
            throw new IllegalArgumentException(
                    "Бронирование должно начинаться и заканчиваться в один день: " +
                            startAt.toLocalDate() + " ≠ " + endAt.toLocalDate()
            );
        }

        LocalTime startTime = startAt.toLocalTime();
        LocalTime endTime = endAt.toLocalTime();

        if (startTime.isBefore(DAY_START)) {
            throw new IllegalArgumentException(
                    "Начало бронирования не может быть раньше " + DAY_START
            );
        }

        if (endTime.isAfter(DAY_END)) {
            throw new IllegalArgumentException(
                    "Конец бронирования не может быть позже " + DAY_END
            );
        }


    }

    private static void validateTimeSlot(LocalDateTime dateTime) {
        if (dateTime.getMinute() % SLOT_MINUTES != 0
                || dateTime.getSecond() != 0
                || dateTime.getNano() != 0) {
            throw new IllegalArgumentException(
                    "Время бронирования должно быть кратно 15 минутам, например 12:00, 12:15, 12:30, 12:45"
            );
        }
    }
}