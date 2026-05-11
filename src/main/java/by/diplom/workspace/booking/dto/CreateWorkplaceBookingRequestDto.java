package by.diplom.workspace.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateWorkplaceBookingRequestDto(
        @NotNull(message = "Укажите рабочее место")
        Long workplaceId,

        @NotNull(message = "Укажите дату начала")
        @Future(message = "Дата начала должна быть в будущем")
        LocalDateTime startAt,

        @NotNull(message = "Укажите дату окончания")
        @Future(message = "Дата окончания должна быть в будущем")
        LocalDateTime endAt
) {
}
