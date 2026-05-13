package by.diplom.workspace.booking.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record WorkplaceBookingUpdateDto(
        @NotNull(message = "Идентификатор брони обязателен")
        UUID bookingId,

        @NotNull(message = "Идентификатор рабочего места обязателен")
        Long workplaceId,

        @NotNull(message = "Дата начала бронирования обязательна")
        LocalDateTime startAt,

        @NotNull(message = "Дата окончания бронирования обязательна")
        LocalDateTime endAt
) {
}
