package by.diplom.workspace.worker.service.user.impl;

import by.diplom.workspace.email.exception.EmailNotFoundException;
import by.diplom.workspace.email.model.UserEmail;
import by.diplom.workspace.notification.component.EmailSender;
import by.diplom.workspace.position.model.DepartmentPosition;
import by.diplom.workspace.worker.dto.profile.request.UpdateNicknameRequestDto;
import by.diplom.workspace.worker.dto.profile.request.UpdatePasswordRequestDto;
import by.diplom.workspace.worker.dto.profile.request.UpdatePublicProfileRequestDto;
import by.diplom.workspace.worker.dto.profile.response.PronounResponseDto;
import by.diplom.workspace.worker.dto.profile.response.UserNicknameResponseDto;
import by.diplom.workspace.worker.dto.profile.response.UserPartPublicProfileResponseDto;
import by.diplom.workspace.worker.dto.profile.response.UserPublicProfileResponseDto;
import by.diplom.workspace.worker.exception.NicknameAlreadyExistsException;
import by.diplom.workspace.worker.exception.UserNotFoundException;
import by.diplom.workspace.worker.exception.password.InvalidPasswordException;
import by.diplom.workspace.worker.exception.password.PasswordMismatchException;
import by.diplom.workspace.worker.model.user.Pronoun;
import by.diplom.workspace.worker.model.user.User;
import by.diplom.workspace.worker.repository.UserRepository;
import by.diplom.workspace.worker.service.user.inter.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailSender emailSender;

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

        emailSender.sendPasswordChangedNotification(user.getPrimaryEmailAddress(), user.getFullName());
        user.changePassword(passwordEncoder.encode(request.newPassword()), emailSender);
    }

    @Override
    @Transactional
    public UserPartPublicProfileResponseDto updatePublicProfile(UUID userId, UpdatePublicProfileRequestDto request) {
        String bio = updateBio(userId, request.bio());
        Pronoun pronoun = updatePronoun(userId, request.pronoun());
        String newPublicEmail = updatePublicEmail(userId, request.email());
        return new UserPartPublicProfileResponseDto(
                bio,
                new PronounResponseDto(pronoun.name(), pronoun.getDisplayName()),
                newPublicEmail
        );
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
                new PronounResponseDto(user.getPronoun().name(), user.getPronoun().getDisplayName()),
                dpInfo,
                publicEmail
        );
    }

    // Вспомогательные методы

    private Pronoun updatePronoun(UUID userId, Pronoun newPronoun) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        user.changePronoun(newPronoun);
        return newPronoun;
    }

    private String updateBio(UUID userId, String newBio) {
        if (!userRepository.existsById(userId))
            throw new UserNotFoundException(userId);

        String normalizedBio = (newBio != null && !newBio.isBlank())
                ? newBio.strip()
                : null;

        userRepository.updateBio(userId, normalizedBio);
        return normalizedBio;
    }

    private String updatePublicEmail(UUID userId, String newPublicEmail) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        boolean belongsToUserAndVerified = user.getEmails().stream()
                .anyMatch(e -> e.getEmail().equals(newPublicEmail) && e.isVerified());

        if (!belongsToUserAndVerified) {
            throw new EmailNotFoundException(newPublicEmail);
        }

        user.changePublicEmail(newPublicEmail);
        return newPublicEmail;
    }


}
