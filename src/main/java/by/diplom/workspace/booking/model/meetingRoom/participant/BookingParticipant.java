package by.diplom.workspace.booking.model.meetingRoom.participant;

import by.diplom.workspace.booking.model.meetingRoom.MeetingRoomBooking;
import by.diplom.workspace.worker.model.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(
        name = "booking_participants",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_booking_participant_user",
                        columnNames = {"meeting_room_booking_id", "user_id"}
                )
        },
        indexes = {
                @Index(name = "idx_booking_participant_booking_id", columnList = "meeting_room_booking_id"),
                @Index(name = "idx_booking_participant_user_id", columnList = "user_id")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookingParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "meeting_room_booking_id", nullable = false)
    private MeetingRoomBooking meetingRoomBooking;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ParticipantStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private ParticipantRole role;

    @Setter(AccessLevel.NONE)
    @Column(name = "response_date")
    private Instant responseDate;


    public BookingParticipant(
            MeetingRoomBooking meetingRoomBooking,
            User user,
            ParticipantRole role
    ) {
        this.meetingRoomBooking = meetingRoomBooking;
        this.user = user;
        this.role = role;
        this.status = ParticipantStatus.PENDING;
    }


    public LocalDateTime getResponseDateInResponseTimeZone() {
        if (responseDate == null || user.getZoneId() == null) {
            return null;
        }

        return LocalDateTime.ofInstant(responseDate, user.getZoneId());
    }


    public void accept() {
        if (this.status != ParticipantStatus.PENDING) {
            throw new IllegalStateException("Ответ на приглашение уже был дан");
        }

        this.status = ParticipantStatus.ACCEPTED;
        setResponseData();
    }

    public void decline() {
        if (this.status != ParticipantStatus.PENDING) {
            throw new IllegalStateException("Ответ на приглашение уже был дан");
        }

        this.status = ParticipantStatus.DECLINED;
        setResponseData();
    }

    private void setResponseData() {
        this.responseDate = Instant.now();
    }
}