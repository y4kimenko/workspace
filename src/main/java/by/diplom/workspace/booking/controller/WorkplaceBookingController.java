package by.diplom.workspace.booking.controller;

import by.diplom.workspace.booking.dto.CreateWorkplaceBookingRequestDto;
import by.diplom.workspace.booking.dto.WorkplaceBookingResponseDto;
import by.diplom.workspace.booking.service.WorkplaceBookingService;
import by.diplom.workspace.security.AppUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/bookings/workplaces")
@RequiredArgsConstructor
public class WorkplaceBookingController {

    private final WorkplaceBookingService service;

    /**
     * Создать бронь
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WorkplaceBookingResponseDto create(
            @AuthenticationPrincipal AppUserDetails currentUser,
            @Valid @RequestBody CreateWorkplaceBookingRequestDto request
    ) {
        return service.create(currentUser.getId(), request);
    }

    /**
     * Отменить свою бронь
     */
    @PatchMapping("/{bookingId}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(
            @AuthenticationPrincipal AppUserDetails currentUser,
            @PathVariable UUID bookingId
    ) {
        service.cancel(currentUser.getId(), bookingId);
    }

    /**
     * Завершить бронь (admin/scheduler)
     */
    @PatchMapping("/{bookingId}/complete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void complete(@PathVariable UUID bookingId) {
        service.complete(bookingId);
    }

    /**
     * Мои бронирования
     */
    @GetMapping("/my")
    public List<WorkplaceBookingResponseDto> myBookings(
            @AuthenticationPrincipal AppUserDetails currentUser
    ) {
        return service.getUserBookings(currentUser.getId());
    }

    /**
     * Бронирования конкретного рабочего места
     */
    @GetMapping("/workplace/{workplaceId}")
    public List<WorkplaceBookingResponseDto> byWorkplace(
            @PathVariable Long workplaceId
    ) {
        return service.getWorkplaceBookings(workplaceId);
    }
}
