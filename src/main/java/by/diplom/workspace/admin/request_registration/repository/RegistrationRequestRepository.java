package by.diplom.workspace.admin.request_registration.repository;

import by.diplom.workspace.admin.request_registration.model.RegistrationRequest;
import by.diplom.workspace.admin.request_registration.model.StatusRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface RegistrationRequestRepository extends JpaRepository<RegistrationRequest, Long> {

    boolean existsByEmailAndStatus(String email, StatusRegistration status);

    Optional<RegistrationRequest> findByEmailAndStatus(String email, StatusRegistration status);

}