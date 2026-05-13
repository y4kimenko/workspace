package by.diplom.workspace.booking.service;

import by.diplom.workspace.booking.dto.FreeSlotDto;
import by.diplom.workspace.booking.dto.WorkplaceBookingCreateDto;
import by.diplom.workspace.booking.dto.WorkplaceBookingResponseDto;
import by.diplom.workspace.booking.dto.WorkplaceBookingUpdateDto;
import by.diplom.workspace.booking.exception.BookingNotFoundException;
import by.diplom.workspace.booking.mapper.WorkplaceBookingMapper;
import by.diplom.workspace.booking.model.workplace.WorkplaceBooking;
import by.diplom.workspace.booking.repository.WorkplaceBookingRepository;
import by.diplom.workspace.place.exception.PlaceNotFoundException;
import by.diplom.workspace.place.model.Workplace;
import by.diplom.workspace.place.repository.WorkplaceRepository;
import by.diplom.workspace.worker.worker.exception.UserNotFoundException;
import by.diplom.workspace.worker.worker.model.user.User;
import by.diplom.workspace.worker.worker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkplaceBookingService {


    private final WorkplaceBookingRepository bookingRepository;
    private final WorkplaceRepository workplaceRepository;
    private final UserRepository userRepository;


    // Создание брони
    @Transactional
    public WorkplaceBookingResponseDto create(WorkplaceBookingCreateDto dto, UUID userId) {

        User user = getUser(userId);
        Workplace workplace = getWorkplace(dto.workplaceId());

        LocalDateTime startAt = dto.startAt();
        LocalDateTime endAt = dto.endAt();

        ZoneId zoneId = user.getZoneId();
        checkOverlap(workplace.getId(), startAt, endAt, zoneId, null);

        WorkplaceBooking booking = new WorkplaceBooking(workplace, user, startAt, endAt);
        bookingRepository.save(booking);

        return WorkplaceBookingMapper.toResponse(booking);
    }

    // Получить все брони пользователя
    @Transactional(readOnly = true)
    public List<WorkplaceBookingResponseDto> getAllByUser(UUID userId) {
        return bookingRepository.findAllByUserId(userId)
                .stream()
                .map(WorkplaceBookingMapper::toResponse)
                .toList();
    }

    // Отмена брони
    @Transactional
    public void cancel(UUID userId, UUID bookingId) {

        WorkplaceBooking booking = bookingRepository
                .findByIdAndUserId(bookingId, userId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));

        booking.cancel();
    }


    // Обновление брони
    @Transactional
    public WorkplaceBookingResponseDto update(WorkplaceBookingUpdateDto dto, UUID userId) {

        WorkplaceBooking booking = bookingRepository
                .findByIdAndUserId(dto.bookingId(), userId)
                .orElseThrow(() -> new BookingNotFoundException(dto.bookingId()));

        // Если меняется рабочее место — заменяем его
        if (!booking.getWorkplace().getId().equals(dto.workplaceId())) {
            Workplace newWorkplace = getWorkplace(dto.workplaceId());
            booking.setWorkplace(newWorkplace);
        }

        LocalDateTime newStart = dto.startAt();
        LocalDateTime newEnd = dto.endAt();

        User user = booking.getCreatedBy();
        checkOverlap(booking.getWorkplace().getId(), newStart, newEnd, user.getZoneId(), booking.getId());

        booking.changePeriod(newStart, newEnd, user.getZoneId());
        return WorkplaceBookingMapper.toResponse(booking);
    }


    // Свободные промежутки времени за день по брони

    /**
     * Возвращает свободные временны́е слоты (кратные 15 мин) на день,
     * который соответствует брони {@code bookingId}.
     * При вычислении слотов бронь {@code bookingId} считается свободной
     * (нужно при редактировании).
     *
     * @param bookingId   идентификатор редактируемой брони (может быть null —
     *                    тогда все занятые брони учитываются)
     * @param workplaceId идентификатор рабочего места
     * @param targetDate  день, для которого вычисляются слоты
     */
    @Transactional(readOnly = true)
    public List<FreeSlotDto> getFreeSlots(UUID bookingId, Long workplaceId, LocalDate targetDate) {

        // Определяем временну́ю зону из брони (или системную по умолчанию)
        ZoneId zoneId = resolveZoneId(bookingId);

        LocalDateTime dayStart = LocalDateTime.of(targetDate, WorkplaceBooking.DAY_START);
        LocalDateTime dayEnd = LocalDateTime.of(targetDate, WorkplaceBooking.DAY_END);

        Instant dayStartInstant = dayStart.atZone(zoneId).toInstant();
        Instant dayEndInstant = dayEnd.atZone(zoneId).toInstant();

        List<WorkplaceBooking> busy = bookingRepository.findActiveForDay(
                workplaceId,
                dayStartInstant,
                dayEndInstant,
                bookingId          // null — не исключаем ничего
        );

        return computeFreeSlots(busy, dayStart, dayEnd, zoneId);
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Private helpers
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Проверяет пересечение с уже существующими активными бронями.
     *
     * @param excludeId UUID брони, которую нужно исключить из проверки (при обновлении)
     */
    private void checkOverlap(
            Long workplaceId,
            LocalDateTime startAt,
            LocalDateTime endAt,
            ZoneId zoneId,
            UUID excludeId
    ) {
        Instant startInstant = startAt.atZone(zoneId).toInstant();
        Instant endInstant = endAt.atZone(zoneId).toInstant();

        boolean hasOverlap = bookingRepository.existsOverlap(
                workplaceId, startInstant, endInstant, excludeId
        );

        if (hasOverlap) {
            throw new IllegalStateException(
                    "Рабочее место уже занято в указанное время"
            );
        }
    }

    /**
     * Вычисляет свободные 15-минутные слоты между занятыми бронями.
     */
    private List<FreeSlotDto> computeFreeSlots(
            List<WorkplaceBooking> busy,
            LocalDateTime dayStart,
            LocalDateTime dayEnd,
            ZoneId zoneId
    ) {
        List<FreeSlotDto> result = new ArrayList<>();

        // Собираем список занятых интервалов в LocalDateTime для удобства
        record Interval(LocalDateTime from, LocalDateTime to) {
        }

        List<Interval> occupied = busy.stream()
                .map(b -> new Interval(
                        b.getStartAt().atZone(zoneId).toLocalDateTime(),
                        b.getEndAt().atZone(zoneId).toLocalDateTime()
                ))
                // Обрезаем по границам дня
                .map(i -> new Interval(
                        i.from().isBefore(dayStart) ? dayStart : i.from(),
                        i.to().isAfter(dayEnd) ? dayEnd : i.to()
                ))
                .toList();

        LocalDateTime cursor = dayStart;

        for (Interval occ : occupied) {
            if (cursor.isBefore(occ.from())) {
                result.add(new FreeSlotDto(cursor, occ.from()));
            }
            if (cursor.isBefore(occ.to())) {
                cursor = occ.to();
            }
        }

        // Остаток дня
        if (cursor.isBefore(dayEnd)) {
            result.add(new FreeSlotDto(cursor, dayEnd));
        }

        return result;
    }

    private ZoneId resolveZoneId(UUID bookingId) {
        if (bookingId == null) return ZoneId.systemDefault();
        return bookingRepository.findById(bookingId)
                .map(b -> b.getCreatedBy().getZoneId())
                .orElse(ZoneId.systemDefault());
    }

    private User getUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    private Workplace getWorkplace(Long workplaceId) {
        return workplaceRepository.findById(workplaceId)
                .orElseThrow(() -> new PlaceNotFoundException(workplaceId));
    }
}