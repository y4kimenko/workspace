package by.diplom.workspace.worker.service.user.impl;

import by.diplom.workspace.email.model.UserEmail;
import by.diplom.workspace.position.model.DepartmentPosition;
import by.diplom.workspace.worker.dto.profile.request.UpdateNicknameRequestDto;
import by.diplom.workspace.worker.dto.profile.request.UpdatePasswordRequestDto;
import by.diplom.workspace.worker.dto.profile.request.UpdatePronounRequestDto;
import by.diplom.workspace.worker.dto.profile.response.UserNicknameResponseDto;
import by.diplom.workspace.worker.dto.profile.response.UserPronounResponseDto;
import by.diplom.workspace.worker.dto.profile.response.UserPublicProfileResponseDto;
import by.diplom.workspace.worker.exception.NicknameAlreadyExistsException;
import by.diplom.workspace.worker.exception.UserNotFoundException;
import by.diplom.workspace.worker.exception.password.InvalidPasswordException;
import by.diplom.workspace.worker.exception.password.PasswordMismatchException;
import by.diplom.workspace.worker.model.user.User;
import by.diplom.workspace.worker.repository.UserRepository;
import by.diplom.workspace.worker.service.user.inter.UserProfileService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserNicknameResponseDto updateNickname(UUID userId, UpdateNicknameRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        String newNickname = request.nickname();

        // Если новый никнейм совпадает с текущим — ничего не делаем
        if (user.getNickname().equals(newNickname)) {
            return new UserNicknameResponseDto(user.getId(), user.getNickname());
        }

        // Никнейм не занят другим пользователем?
        if (userRepository.existsByNickname(newNickname)) {
            throw new NicknameAlreadyExistsException(newNickname);
        }

        user.changeNickname(newNickname);

        return new UserNicknameResponseDto(user.getId(), user.getNickname());
    }

    @Override
    @Transactional
    public UserPronounResponseDto updatePronoun(UUID userId, UpdatePronounRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        user.changePronoun(request.pronoun());

        return new UserPronounResponseDto(
                user.getId(),
                user.getPronoun(),
                user.getPronoun().getDisplayName()
        );
    }

    @Override
    @Transactional
    public void updatePassword(UUID userId, UpdatePasswordRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // Проверяем, что новый пароль и подтверждение совпадают
        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new PasswordMismatchException();
        }

        // Проверяем, что текущий пароль введён верно
        if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            throw new InvalidPasswordException();
        }

        // Проверяем, что новый пароль не совпадает со старым
        if (passwordEncoder.matches(request.newPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Новый пароль не должен совпадать с текущим");
        }

        user.changePassword(passwordEncoder.encode(request.newPassword()));
    }

    @Override
    @Transactional(readOnly = true)
    public UserPublicProfileResponseDto getMyProfile(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // Должность и отдел
        UserPublicProfileResponseDto.DepartmentPositionInfo dpInfo = null;
        if (user.getDepartmentPosition() != null) {
            DepartmentPosition dp = user.getDepartmentPosition();
            dpInfo = new UserPublicProfileResponseDto.DepartmentPositionInfo(
                    dp.getPosition() != null ? dp.getPosition().getName() : null,
                    dp.getDepartment() != null ? dp.getDepartment().getName() : null
            );
        }

        // Публичный email
        String publicEmail = user.getEmails().stream()
                .filter(UserEmail::isPublicEmail)
                .map(UserEmail::getEmail)
                .findFirst()
                .orElse(null);


        return new UserPublicProfileResponseDto(
                user.getFullName(),
                user.getNickname(),
                user.getAvatarPath(),
                user.getBio(),
                user.getPronoun().getDisplayName(),
                dpInfo,
                publicEmail
        );
    }
}
