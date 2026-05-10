package by.diplom.workspace.worker.repository;


import by.diplom.workspace.worker.model.user.profile.position.DepartmentPosition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DepartmentPositionRepository  extends JpaRepository<DepartmentPosition, Long> {
}
