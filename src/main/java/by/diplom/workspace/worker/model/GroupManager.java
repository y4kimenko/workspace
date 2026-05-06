package by.diplom.workspace.worker.model;

import by.diplom.workspace.worker.model.user.profile.position.DepartmentPosition;
import by.diplom.workspace.worker.model.user.User;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
@DiscriminatorValue("GROUP_MANAGER")
public class GroupManager extends User {

    @OneToMany(mappedBy = "groupManager")
    private final List<Employee> employees = new ArrayList<>();

    public GroupManager(
            String fullName,
            String nickname,
            String passwordHash,
            String timezone,
            DepartmentPosition departmentPosition
    ) {
        super(fullName, nickname, passwordHash, timezone, departmentPosition);
    }

    public void addEmployee(Employee employee) {
        employees.add(employee);
        employee.setGroupManager(this);
    }

    public void removeEmployee(Employee employee) {
        employees.remove(employee);
        employee.setGroupManager(null);
    }
}