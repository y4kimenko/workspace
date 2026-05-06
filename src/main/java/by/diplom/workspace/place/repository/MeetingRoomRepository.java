package by.diplom.workspace.place.repository;

import by.diplom.workspace.place.model.MeetingRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MeetingRoomRepository extends JpaRepository<MeetingRoom, UUID> {
}