package by.diplom.workspace.booking.dto;

import by.diplom.workspace.booking.model.workplace.WorkplaceBookingStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record WorkplaceBookingResponseDto(
        UUID id,
        Long workplaceId,
        Integer floor,
        Integer workplaceNumber,
        LocalDateTime startAt,
        LocalDateTime endAt,
        WorkplaceBookingStatus status
) {
}
