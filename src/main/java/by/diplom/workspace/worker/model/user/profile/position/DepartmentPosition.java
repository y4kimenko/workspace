package by.diplom.workspace.worker.model.user.profile.position;

import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
        name = "department_positions",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_department_position",
                        columnNames = {"department_id", "position_id"}
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DepartmentPosition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "position_id", nullable = false)
    private Position position;

    public DepartmentPosition(Department department, Position position) {
        this.department = department;
        this.position = position;
    }
}