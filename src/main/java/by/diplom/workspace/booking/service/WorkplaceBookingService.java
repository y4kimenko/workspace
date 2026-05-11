package by.diplom.workspace.booking.service;

import by.diplom.workspace.booking.dto.CreateWorkplaceBookingRequestDto;
import by.diplom.workspace.booking.dto.WorkplaceBookingResponseDto;
import by.diplom.workspace.booking.exception.BookingConflictException;
import by.diplom.workspace.booking.mapper.WorkplaceBookingMapper;
import by.diplom.workspace.booking.model.workplace.WorkplaceBooking;
import by.diplom.workspace.booking.repository.WorkplaceBookingRepository;
import by.diplom.workspace.place.model.Workplace;
import by.diplom.workspace.place.repository.WorkplaceRepository;
import by.diplom.workspace.worker.model.user.User;
import by.diplom.workspace.worker.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkplaceBookingService {

    private final WorkplaceBookingRepository bookingRepository;
    private final WorkplaceRepository workplaceRepository;
    private final UserRepository userRepository;
    private final WorkplaceBookingMapper mapper;

    @Transactional
    public WorkplaceBookingResponseDto create(UUID userId, CreateWorkplaceBookingRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        Workplace workplace = workplaceRepository.findById(request.workplaceId())
                .orElseThrow(() -> new EntityNotFoundException("Рабочее место не найдено"));

        // Конвертируем локальное время пользователя в Instant для проверки конфликтов
        ZoneId zoneId = user.getZoneId();
        var startInstant = request.startAt().atZone(zoneId).toInstant();
        var endInstant = request.endAt().atZone(zoneId).toInstant();

        if (bookingRepository.existsConflict(workplace.getId(), startInstant, endInstant)) {
            throw new BookingConflictException(
                    "Рабочее место уже занято в выбранный период"
            );
        }

        WorkplaceBooking booking = new WorkplaceBooking(
                workplace, user, request.startAt(), request.endAt()
        );

        return mapper.toResponse(bookingRepository.save(booking));
    }

    @Transactional
    public void cancel(UUID userId, UUID bookingId) {
        WorkplaceBooking booking = getBookingAndCheckOwnership(userId, bookingId);
        booking.cancel();
    }

    @Transactional
    public void complete(UUID bookingId) {
        WorkplaceBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Бронирование не найдено"));
        booking.complete();
    }

    @Transactional(readOnly = true)
    public List<WorkplaceBookingResponseDto> getUserBookings(UUID userId) {
        return bookingRepository.findByCreatedByIdOrderByStartAtDesc(userId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<WorkplaceBookingResponseDto> getWorkplaceBookings(Long workplaceId) {
        return bookingRepository.findByWorkplaceIdOrderByStartAtAsc(workplaceId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    private WorkplaceBooking getBookingAndCheckOwnership(UUID userId, UUID bookingId) {
        WorkplaceBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Бронирование не найдено"));

        if (!booking.getCreatedBy().getId().equals(userId)) {
            throw new AccessDeniedException("Нет доступа к этому бронированию");
        }
        return booking;
    }
}