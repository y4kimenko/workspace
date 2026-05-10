package by.diplom.workspace.worker.model;

import by.diplom.workspace.worker.model.user.User;
import by.diplom.workspace.worker.model.user.profile.position.DepartmentPosition;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@DiscriminatorValue("EMPLOYEE")
public class Employee extends User {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_manager_id")
    private GroupManager groupManager;

    public Employee(
            String fullName,
            String nickname,
            String passwordHash,
            DepartmentPosition departmentPosition
    ) {
        super(fullName, nickname, passwordHash, departmentPosition);
    }
}
