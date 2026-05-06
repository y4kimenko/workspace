package by.diplom.workspace.model;

import by.diplom.workspace.model.user.profile.position.DepartmentPosition;
import by.diplom.workspace.model.user.User;
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
            String timezone,
            String passwordHash,
            DepartmentPosition departmentPosition
    ) {
        super(fullName, nickname, passwordHash, timezone, departmentPosition);
    }
}
