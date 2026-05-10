package by.diplom.workspace.admin.service;

import by.diplom.workspace.admin.component.NicknameGenerator;
import by.diplom.workspace.admin.component.PasswordGenerator;
import by.diplom.workspace.admin.dto.request.CreateUserRequest;
import by.diplom.workspace.admin.dto.response.CreateUserResponse;
import by.diplom.workspace.email.component.EmailSender;
import by.diplom.workspace.worker.model.Employee;
import by.diplom.workspace.worker.model.GroupManager;
import by.diplom.workspace.worker.model.user.User;
import by.diplom.workspace.worker.model.user.profile.position.DepartmentPosition;
import by.diplom.workspace.worker.repository.DepartmentPositionRepository;
import by.diplom.workspace.worker.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
    public CreateUserResponse createUser(CreateUserRequest request) {
        DepartmentPosition position = departmentPositionRepository
                .findById(request.departmentPositionId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Position not found: " + request.departmentPositionId()
                ));

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

        return new CreateUserResponse(user.getId(), nickname);
    }
}
