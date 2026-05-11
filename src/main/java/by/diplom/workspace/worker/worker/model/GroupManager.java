package by.diplom.workspace.worker.worker.model;


import by.diplom.workspace.booking.model.meetingRoom.MeetingRoomBooking;
import by.diplom.workspace.worker.position.model.DepartmentPosition;
import by.diplom.workspace.worker.worker.model.user.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Entity
@NoArgsConstructor
@DiscriminatorValue("GROUP_MANAGER")
public class GroupManager extends User {

    @OneToMany(mappedBy = "groupManager")
    private final Set<Employee> employees = new HashSet<>();

    @OneToMany(
            mappedBy = "createdBy",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private final List<MeetingRoomBooking> bookingsMeetingRoom = new ArrayList<>();

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