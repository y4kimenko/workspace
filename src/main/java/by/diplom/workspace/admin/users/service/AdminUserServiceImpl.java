package by.diplom.workspace.admin.users.service;

import by.diplom.workspace.admin.request_registration.exception.DepartmentPositionNotFoundException;
import by.diplom.workspace.admin.users.dto.request.CreateUserRequestDto;
import by.diplom.workspace.admin.users.dto.response.CreateUserResponseDto;
import by.diplom.workspace.worker.notification.component.EmailSender;
import by.diplom.workspace.worker.position.model.DepartmentPosition;
import by.diplom.workspace.worker.position.repository.DepartmentPositionRepository;
import by.diplom.workspace.worker.worker.component.NicknameGenerator;
import by.diplom.workspace.worker.worker.component.PasswordGenerator;
import by.diplom.workspace.worker.worker.model.Employee;
import by.diplom.workspace.worker.worker.model.GroupManager;
import by.diplom.workspace.worker.worker.model.user.User;
import by.diplom.workspace.worker.worker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {
    private final UserRepository userRepository;
    private final DepartmentPositionRepository departmentPositionRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordGenerator passwordGenerator;
    private final NicknameGenerator nicknameGenerator;
    private final EmailSender emailSender;

    @Transactional
    @Override
    public CreateUserResponseDto createUser(CreateUserRequestDto request) {
        long departamentId = request.departmentPosition().departmentId();
        long potionId = request.departmentPosition().positionId();

        DepartmentPosition position = departmentPositionRepository
                .findByDepartment_IdAndPosition_Id(
                        departamentId,
                        potionId
                )
                .orElseThrow(
                        () -> new DepartmentPositionNotFoundException(departamentId, potionId)
                );

        String rawPassword = passwordGenerator.generate();
        String passwordHash = passwordEncoder.encode(rawPassword);
        String nickname = nicknameGenerator.generate(request.fullName());

        User user = switch (request.userType()) {
            case EMPLOYEE -> new Employee(
                    request.fullName(), nickname, passwordHash, position
            );
            case GROUP_MANAGER -> new GroupManager(
                    request.fullName(), nickname, passwordHash, position
            );
        };

        // Добавляем email как подтверждённый и основной —
        // администратор сам вводит валидный email
        user.addEmail(request.email(), true, true);

        userRepository.save(user);

        // Отправляем письмо после успешного сохранения
        emailSender.sendWelcomeEmail(request.email(), request.fullName(), nickname, rawPassword);

        return new CreateUserResponseDto(user.getId(), nickname);
    }


}
