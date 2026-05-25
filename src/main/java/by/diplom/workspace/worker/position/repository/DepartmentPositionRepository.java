package by.diplom.workspace.worker.position.repository;


import by.diplom.workspace.worker.position.model.DepartmentPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DepartmentPositionRepository extends JpaRepository<DepartmentPosition, Long> {
    List<DepartmentPosition> findAllByDepartmentId(Long departmentId);

    List<DepartmentPosition> findAllByPositionId(Long positionId);

    boolean existsByDepartmentIdAndPositionId(Long departmentId, Long positionId);

    boolean existsByDepartmentIdAndPositionIdAndIdNot(
            Long departmentId, Long positionId, Long id
    );

    Optional<DepartmentPosition> findByDepartment_IdAndPosition_Id(Long departmentId, Long positionId);

    // Обнуляем department у всех связок этого отдела
    @Modifying
    @Query("UPDATE DepartmentPosition dp SET dp.department = null WHERE dp.department.id = :departmentId")
    void nullifyDepartment(@Param("departmentId") Long departmentId);

    // Обнуляем position у всех связок этой должности
    @Modifying
    @Query("UPDATE DepartmentPosition dp SET dp.position = null WHERE dp.position.id = :positionId")
    void nullifyPosition(@Param("positionId") Long positionId);
}
