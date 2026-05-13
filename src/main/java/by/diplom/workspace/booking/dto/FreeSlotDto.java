package by.diplom.workspace.booking.dto;

import java.time.LocalDateTime;

public record FreeSlotDto(
        LocalDateTime from,
        LocalDateTime to
) {
}
