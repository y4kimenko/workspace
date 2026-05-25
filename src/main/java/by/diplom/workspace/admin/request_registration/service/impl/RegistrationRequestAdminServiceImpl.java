package by.diplom.workspace.admin.request_registration.service.impl;

import by.diplom.workspace.admin.request_registration.dto.ApproveRegistrationRequestDto;
import by.diplom.workspace.admin.request_registration.dto.RejectRegistrationRequestDto;
import by.diplom.workspace.admin.request_registration.exception.DepartmentPositionNotFoundException;
import by.diplom.workspace.admin.request_registration.exception.RegistrationRequestAlreadyCancelledException;
import by.diplom.workspace.admin.request_registration.exception.RegistrationRequestAlreadyCreatedException;
import by.diplom.workspace.admin.request_registration.exception.RegistrationRequestEmailNotVerifiedException;
import by.diplom.workspace.admin.request_registration.exception.RegistrationRequestNotFoundException;
import by.diplom.workspace.admin.request_registration.model.RegistrationRequest;
import by.diplom.workspace.admin.request_registration.model.StatusRegistration;
import by.diplom.workspace.admin.request_registration.repository.RegistrationRequestRepository;
import by.diplom.workspace.admin.request_registration.service.RegistrationRequestAdminService;
import by.diplom.workspace.worker.notification.component.EmailSender;
import by.diplom.workspace.worker.position.model.DepartmentPosition;
import by.diplom.workspace.worker.position.repository.DepartmentPositionRepository;
import by.diplom.workspace.worker.worker.component.NicknameGenerator;
import by.diplom.workspace.worker.worker.component.PasswordGenerator;
import by.diplom.workspace.worker.worker.dto.user.response.CreateUserResponseDto;
import by.diplom.workspace.worker.worker.model.Employee;
import by.diplom.workspace.worker.worker.model.GroupManager;
import by.diplom.workspace.worker.worker.model.user.User;
import by.diplom.workspace.worker.worker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegistrationRequestAdminServiceImpl implements RegistrationRequestAdminService {
    private final RegistrationRequestRepository registrationRequestRepository;
    private final DepartmentPositionRepository departmentPositionRepository;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordGenerator passwordGenerator;
    private final NicknameGenerator nicknameGenerator;
    private final EmailSender emailSender;

    @Override
    public CreateUserResponseDto approve(Long registrationRequestId, ApproveRegistrationRequestDto requestDto) {
        // 1. Находим активную заявку по email
        RegistrationRequest registrationRequest = registrationRequestRepository
                .findById(registrationRequestId)
                .orElseThrow(
                        () -> new RegistrationRequestNotFoundException(registrationRequestId)
                );
        if (!registrationRequest.isEmailIsVerified())
            throw new RegistrationRequestEmailNotVerifiedException(registrationRequest.getEmail());

        if (!registrationRequest.getFullName().equals(requestDto.fullName()))
            registrationRequest.setFullName(requestDto.fullName());


        Long departmentId = requestDto.departmentPosition().departmentId();
        Long positionId = requestDto.departmentPosition().positionId();
        DepartmentPosition departmentPositionRequest = departmentPositionRepository.findByDepartment_IdAndPosition_Id(
                departmentId,
                positionId
        ).orElseThrow(
                () -> new DepartmentPositionNotFoundException(departmentId, positionId)
        );
        if (!registrationRequest.getDepartmentPosition().equals(departmentPositionRequest))
            registrationRequest.setDepartmentPosition(departmentPositionRequest);

        registrationRequestRepository.save(registrationRequest);

        String rawPassword = passwordGenerator.generate();
        String passwordHash = passwordEncoder.encode(rawPassword);
        String nickname = nicknameGenerator.generate(requestDto.fullName());

        User user = switch (requestDto.userType()) {
            case EMPLOYEE -> new Employee(
                    requestDto.fullName(), nickname, passwordHash, departmentPositionRequest
            );
            case GROUP_MANAGER -> new GroupManager(
                    requestDto.fullName(), nickname, passwordHash, departmentPositionRequest
            );
        };

        // Добавляем email как подтверждённый и основной —

        user.addEmail(registrationRequest.getEmail(), true, true);

        userRepository.save(user);

        // Отправляем письмо после успешного сохранения
        emailSender.sendWelcomeEmail(
                registrationRequest.getEmail(),
                registrationRequest.getFullName(),
                nickname,
                rawPassword
        );

        return new CreateUserResponseDto(user.getId(), nickname);

    }

    @Override
    public void reject(Long registrationRequestId, RejectRegistrationRequestDto requestDto) {
        RegistrationRequest registrationRequest = registrationRequestRepository
                .findById(registrationRequestId)
                .orElseThrow(
                        () -> new RegistrationRequestNotFoundException(registrationRequestId)
                );

        if (!registrationRequest.isEmailIsVerified())
            throw new RegistrationRequestEmailNotVerifiedException(registrationRequest.getEmail());

        if (registrationRequest.getStatus() == StatusRegistration.CREATED)
            throw new RegistrationRequestAlreadyCreatedException(registrationRequestId);

        if (registrationRequest.getStatus() == StatusRegistration.CANCELLED)
            throw new RegistrationRequestAlreadyCancelledException(registrationRequestId);


        registrationRequest.setStatus(StatusRegistration.CANCELLED);
        registrationRequestRepository.save(registrationRequest);


        String response = requestDto.reason().isEmpty() ?
                "Заявка была отклонена по результатам рассмотрения администратором."
                : requestDto.reason();
        emailSender.sendAccountCreationRequestRejectedPushNotification(
                registrationRequest.getEmail(),
                registrationRequest.getFullName(),
                response
        );

    }
}
