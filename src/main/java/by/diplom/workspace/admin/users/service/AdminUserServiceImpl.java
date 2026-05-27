package by.diplom.workspace.admin.users.service;

import by.diplom.workspace.admin.request_registration.exception.DepartmentPositionNotFoundException;
import by.diplom.workspace.admin.users.dto.EnumsDto.UserTypeRequest;
import by.diplom.workspace.admin.users.dto.request.CreateUserRequestDto;
import by.diplom.workspace.admin.users.dto.request.UserUpdateRequestDto;
import by.diplom.workspace.admin.users.dto.response.CreateUserResponseDto;
import by.diplom.workspace.admin.users.dto.response.UserResponseDto;
import by.diplom.workspace.worker.notification.component.EmailSender;
import by.diplom.workspace.worker.position.model.DepartmentPosition;
import by.diplom.workspace.worker.position.repository.DepartmentPositionRepository;
import by.diplom.workspace.worker.worker.component.NicknameGenerator;
import by.diplom.workspace.worker.worker.component.PasswordGenerator;
import by.diplom.workspace.worker.worker.exception.NicknameAlreadyExistsException;
import by.diplom.workspace.worker.worker.exception.UserNotFoundException;
import by.diplom.workspace.worker.worker.mapper.UserMapper;
import by.diplom.workspace.worker.worker.model.Employee;
import by.diplom.workspace.worker.worker.model.GroupManager;
import by.diplom.workspace.worker.worker.model.user.User;
import by.diplom.workspace.worker.worker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;


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

    @Override
    public void deleteUser(UUID id) {
        if (userRepository.existsById(id))
            userRepository.deleteById(id);
        else
            throw new UserNotFoundException(id);
    }

    @Override
    @Transactional
    public UserResponseDto update(UUID id, UserUpdateRequestDto request) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException(id)
        );

        if (!user.getNickname().equals(request.nickName())
                && userRepository.existsUserByNickname(request.nickName())) {
            throw new NicknameAlreadyExistsException(request.nickName());
        }

        long departmentId = request.departmentPosition().departmentId();
        long positionId = request.departmentPosition().positionId();

        DepartmentPosition departmentPosition = departmentPositionRepository
                .findByDepartment_IdAndPosition_Id(departmentId, positionId)
                .orElseThrow(
                        () -> new DepartmentPositionNotFoundException(departmentId, positionId)
                );

        user.setFullName(request.fullName());
        user.changeNickname(request.nickName());
        user.setDepartmentPosition(departmentPosition);

        if (isUserTypeChanged(user, request.userType())) {
            validateUserTypeChange(user, request.userType());

            userRepository.flush();

            userRepository.updateUserType(
                    user.getId(),
                    request.userType().name()
            );

            user = userRepository.findById(id).orElseThrow(
                    () -> new UserNotFoundException(id)
            );
        }

        return UserMapper.toUserResponseDto(user);
    }

    @Override
    public List<UserResponseDto> getAllUser() {
        return userRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt")).stream()
                .map(UserMapper::toUserResponseDto)
                .toList();
    }

    private void validateUserTypeChange(User user, UserTypeRequest requestedType) {
        if (user instanceof GroupManager groupManager
                && requestedType == UserTypeRequest.EMPLOYEE) {

            if (!groupManager.getEmployees().isEmpty()) {
                throw new IllegalStateException(
                        "Нельзя изменить руководителя группы на сотрудника, пока за ним закреплены сотрудники"
                );
            }

            if (!groupManager.getBookingsMeetingRoom().isEmpty()) {
                throw new IllegalStateException(
                        "Нельзя изменить руководителя группы на сотрудника, пока у него есть бронирования переговорных комнат"
                );
            }
        }
    }

    private boolean isUserTypeChanged(User user, UserTypeRequest requestedType) {
        return switch (requestedType) {
            case EMPLOYEE -> !(user instanceof Employee);
            case GROUP_MANAGER -> !(user instanceof GroupManager);
        };
    }
}
