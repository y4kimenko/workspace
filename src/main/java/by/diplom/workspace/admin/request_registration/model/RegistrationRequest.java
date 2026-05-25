package by.diplom.workspace.admin.request_registration.model;

import by.diplom.workspace.worker.position.model.DepartmentPosition;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "registration_requests")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RegistrationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_position_id", nullable = false)
    private DepartmentPosition departmentPosition;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "email_is_verified", nullable = false)
    private boolean emailIsVerified = false;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusRegistration status = StatusRegistration.WAITING;

    public RegistrationRequest(String fullName, DepartmentPosition departmentPosition, String email) {
        this.fullName = fullName;
        this.departmentPosition = departmentPosition;
        this.email = email;
        this.createdAt = Instant.now();
    }


}