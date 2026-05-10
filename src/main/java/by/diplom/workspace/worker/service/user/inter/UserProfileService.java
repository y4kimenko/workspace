package by.diplom.workspace.worker.service.user.inter;

import by.diplom.workspace.worker.dto.profile.request.UpdateNicknameRequestDto;
import by.diplom.workspace.worker.dto.profile.request.UpdatePasswordRequestDto;
import by.diplom.workspace.worker.dto.profile.request.UpdatePronounRequestDto;
import by.diplom.workspace.worker.dto.profile.response.UserNicknameResponseDto;
import by.diplom.workspace.worker.dto.profile.response.UserPronounResponseDto;

import java.util.UUID;

public interface UserProfileService {
    UserNicknameResponseDto updateNickname(UUID userId, UpdateNicknameRequestDto request);

    UserPronounResponseDto updatePronoun(UUID userId, UpdatePronounRequestDto request);

    void updatePassword(UUID userId, UpdatePasswordRequestDto request);


}
