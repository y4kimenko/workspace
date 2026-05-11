package by.diplom.workspace.booking.mapper;

import by.diplom.workspace.booking.dto.WorkplaceBookingResponseDto;
import by.diplom.workspace.booking.model.workplace.WorkplaceBooking;
import by.diplom.workspace.worker.model.user.User;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;

@Component
public class WorkplaceBookingMapper {
    public WorkplaceBookingResponseDto toResponse(WorkplaceBooking booking) {
        return new WorkplaceBookingResponseDto(
                booking.getId(),
                booking.getWorkplace().getId(),
                booking.getWorkplace().getFloor(),
                booking.getWorkplace().getNumber(),
                booking.getCreatedBy().getId(),
                booking.getCreatedBy().getFullName(),
                toLocalDateTime(booking.getStartAt(), booking.getCreatedBy()),
                toLocalDateTime(booking.getEndAt(), booking.getCreatedBy()),
                booking.getStatus()
        );
    }

    private LocalDateTime toLocalDateTime(Instant instant, User user) {
        return instant.atZone(user.getZoneId()).toLocalDateTime();
    }
}