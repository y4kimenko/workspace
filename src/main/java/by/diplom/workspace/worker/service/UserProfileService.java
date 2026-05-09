package by.diplom.workspace.worker.service;

import by.diplom.workspace.worker.dto.request.UpdateNicknameRequest;
import by.diplom.workspace.worker.dto.request.UpdatePasswordRequest;
import by.diplom.workspace.worker.dto.request.UpdatePronounRequest;
import by.diplom.workspace.worker.dto.response.UserNicknameResponse;
import by.diplom.workspace.worker.dto.response.UserPronounResponse;

import java.util.UUID;

public interface UserProfileService {
    public UserNicknameResponse updateNickname(UUID userId, UpdateNicknameRequest request);

    public UserPronounResponse updatePronoun(UUID userId, UpdatePronounRequest request);

    public void updatePassword(UUID userId, UpdatePasswordRequest request);
}
