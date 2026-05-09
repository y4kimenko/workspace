package by.diplom.workspace.worker.service.impl;

import by.diplom.workspace.worker.dto.request.UpdateNicknameRequest;
import by.diplom.workspace.worker.dto.request.UpdatePasswordRequest;
import by.diplom.workspace.worker.dto.request.UpdatePronounRequest;
import by.diplom.workspace.worker.dto.response.UserNicknameResponse;
import by.diplom.workspace.worker.dto.response.UserPronounResponse;
import by.diplom.workspace.worker.exception.NicknameAlreadyExistsException;
import by.diplom.workspace.worker.exception.UserNotFoundException;
import by.diplom.workspace.worker.exception.password.InvalidPasswordException;
import by.diplom.workspace.worker.exception.password.PasswordMismatchException;
import by.diplom.workspace.worker.model.user.User;
import by.diplom.workspace.worker.repository.UserRepository;
import by.diplom.workspace.worker.service.UserProfileService;
import jakarta.transaction.Transactional;
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
    public UserNicknameResponse updateNickname(UUID userId, UpdateNicknameRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        String newNickname = request.nickname();

        // Если новый никнейм совпадает с текущим — ничего не делаем
        if (user.getNickname().equals(newNickname)) {
            return new UserNicknameResponse(user.getId(), user.getNickname());
        }

        // Никнейм не занят другим пользователем?
        if (userRepository.existsByNickname(newNickname)) {
            throw new NicknameAlreadyExistsException(newNickname);
        }

        user.changeNickname(newNickname);

        return new UserNicknameResponse(user.getId(), user.getNickname());
    }

    @Override
    @Transactional
    public UserPronounResponse updatePronoun(UUID userId, UpdatePronounRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        user.changePronoun(request.pronoun());

        return new UserPronounResponse(
                user.getId(),
                user.getPronoun(),
                user.getPronoun().getDisplayName()
        );
    }

    @Override
    @Transactional
    public void updatePassword(UUID userId, UpdatePasswordRequest request) {
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
}
