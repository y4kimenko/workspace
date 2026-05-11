package by.diplom.workspace.booking.dto;

import by.diplom.workspace.booking.model.workplace.WorkplaceBookingStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record WorkplaceBookingResponseDto(
        UUID id,
        Long workplaceId,
        Integer workplaceFloor,
        Integer workplaceNumber,
        UUID createdById,
        String createdByFullName,
        LocalDateTime startAt,
        LocalDateTime endAt,
        WorkplaceBookingStatus status
) {
}
