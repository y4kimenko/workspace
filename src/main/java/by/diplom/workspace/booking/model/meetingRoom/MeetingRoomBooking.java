package by.diplom.workspace.booking.model.meetingRoom;

import by.diplom.workspace.booking.model.Booking;
import by.diplom.workspace.booking.model.meetingRoom.participant.BookingParticipant;
import by.diplom.workspace.booking.model.meetingRoom.participant.ParticipantRole;
import by.diplom.workspace.place.model.MeetingRoom;
import by.diplom.workspace.worker.worker.model.GroupManager;
import by.diplom.workspace.worker.worker.model.user.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(
        name = "meeting_room_bookings",
        indexes = {
                @Index(name = "idx_meeting_room_booking_room_id", columnList = "meeting_room_id"),
                @Index(name = "idx_meeting_room_booking_created_by_id", columnList = "created_by_id"),
                @Index(name = "idx_meeting_room_booking_time", columnList = "start_at, end_at")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MeetingRoomBooking extends Booking {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "meeting_room_id", nullable = false)
    private MeetingRoom meetingRoom;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by_id", nullable = false)
    private GroupManager createdBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MeetingRoomBookingStatus status;

    @OneToMany(
            mappedBy = "meetingRoomBooking",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<BookingParticipant> participants = new HashSet<>();

    public MeetingRoomBooking(
            MeetingRoom meetingRoom,
            GroupManager createdBy,
            LocalDateTime startAt,
            LocalDateTime endAt

    ) {
        super(startAt, endAt, createdBy.getZoneId());
        this.meetingRoom = meetingRoom;
        this.createdBy = createdBy;
        this.status = MeetingRoomBookingStatus.PENDING_RESPONSES;
    }

    public void addParticipant(User user, ParticipantRole role) {
        boolean alreadyAdded = participants.stream()
                .anyMatch(participant -> participant.getUser().equals(user));

        if (alreadyAdded) {
            throw new IllegalStateException("Сотрудник уже добавлен в список участников");
        }

        BookingParticipant participant = new BookingParticipant(
                this,
                user,
                role
        );

        participants.add(participant);
    }

    public void confirm() {
        if (this.status == MeetingRoomBookingStatus.CANCELLED) {
            throw new IllegalStateException("Нельзя подтвердить отменённое бронирование переговорной");
        }

        if (this.status == MeetingRoomBookingStatus.COMPLETED) {
            throw new IllegalStateException("Нельзя подтвердить завершённое бронирование переговорной");
        }

        this.status = MeetingRoomBookingStatus.CONFIRMED;
    }

    public void cancel() {
        if (this.status == MeetingRoomBookingStatus.COMPLETED) {
            throw new IllegalStateException("Нельзя отменить завершённое бронирование переговорной");
        }

        this.status = MeetingRoomBookingStatus.CANCELLED;
    }

    public void complete() {
        if (this.status == MeetingRoomBookingStatus.CANCELLED) {
            throw new IllegalStateException("Нельзя завершить отменённое бронирование переговорной");
        }

        this.status = MeetingRoomBookingStatus.COMPLETED;
    }
}