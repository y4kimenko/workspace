package by.diplom.workspace.worker.service.user.inter;

import by.diplom.workspace.worker.dto.profile.request.UpdateNicknameRequestDto;
import by.diplom.workspace.worker.dto.profile.request.UpdatePasswordRequestDto;
import by.diplom.workspace.worker.dto.profile.request.UpdatePublicProfileRequestDto;
import by.diplom.workspace.worker.dto.profile.response.UserNicknameResponseDto;
import by.diplom.workspace.worker.dto.profile.response.UserPartPublicProfileResponseDto;
import by.diplom.workspace.worker.dto.profile.response.UserPublicProfileResponseDto;

import java.util.UUID;

public interface UserProfileService {
    UserNicknameResponseDto updateNickname(UUID userId, UpdateNicknameRequestDto request);

    void updatePassword(UUID userId, UpdatePasswordRequestDto request);

    UserPublicProfileResponseDto getMyProfile(UUID userId);

    UserPartPublicProfileResponseDto updatePublicProfile(UUID userId, UpdatePublicProfileRequestDto request);

}
