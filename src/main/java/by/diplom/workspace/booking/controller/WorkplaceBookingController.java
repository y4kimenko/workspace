package by.diplom.workspace.booking.controller;

import by.diplom.workspace.booking.dto.WorkplaceBookingCreateDto;
import by.diplom.workspace.booking.dto.FreeSlotDto;
import by.diplom.workspace.booking.dto.WorkplaceBookingResponseDto;
import by.diplom.workspace.booking.dto.WorkplaceBookingUpdateDto;
import by.diplom.workspace.booking.service.WorkplaceBookingService;
import by.diplom.workspace.security.AppUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/bookings/workplaces")
@RequiredArgsConstructor
public class WorkplaceBookingController {

    private final WorkplaceBookingService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'GROUP_MANAGER')")
    public WorkplaceBookingResponseDto create(
            @Valid @RequestBody WorkplaceBookingCreateDto dto,
            @AuthenticationPrincipal AppUserDetails currentUser
    ) {
        return service.create(dto, currentUser.getId());

    }

    @PatchMapping("/{bookingId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'GROUP_MANAGER')")
    public void cancel(
            @AuthenticationPrincipal AppUserDetails currentUser,
            @PathVariable UUID bookingId
    ) {
        service.cancel(currentUser.getId(), bookingId);
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'GROUP_MANAGER')")
    public WorkplaceBookingResponseDto update(
            @Valid @RequestBody WorkplaceBookingUpdateDto dto,
            @AuthenticationPrincipal AppUserDetails currentUser
    ) {
        return service.update(dto, currentUser.getId());
    }


    @GetMapping("/my")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'GROUP_MANAGER')")
    public List<WorkplaceBookingResponseDto> myBookings(
            @AuthenticationPrincipal AppUserDetails currentUser
    ) {
        return service.getAllByUser(currentUser.getId());
    }

    @GetMapping("/free-slots")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'GROUP_MANAGER')")
    public List<FreeSlotDto> getFreeSlots(
            @RequestParam(required = false) UUID bookingId,
            @RequestParam Long workplaceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return service.getFreeSlots(bookingId, workplaceId, date);
    }
}
