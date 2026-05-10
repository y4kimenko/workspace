package by.diplom.workspace.worker.model;

import by.diplom.workspace.worker.model.user.User;
import by.diplom.workspace.position.model.DepartmentPosition;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Getter
@Entity
@NoArgsConstructor
@DiscriminatorValue("GROUP_MANAGER")
public class GroupManager extends User {

    @OneToMany(mappedBy = "groupManager")
    private final Set<Employee> employees = new HashSet<>();

    public GroupManager(
            String fullName,
            String nickname,
            String passwordHash,
            DepartmentPosition departmentPosition
    ) {
        super(fullName, nickname, passwordHash, departmentPosition);
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