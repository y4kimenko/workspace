package by.diplom.workspace.worker.worker.controller.user;

import by.diplom.workspace.security.AppUserDetails;
import by.diplom.workspace.worker.worker.dto.profile.request.UpdateNicknameRequestDto;
import by.diplom.workspace.worker.worker.dto.profile.request.UpdatePasswordRequestDto;
import by.diplom.workspace.worker.worker.dto.profile.response.UserNicknameResponseDto;
import by.diplom.workspace.worker.worker.service.user.inter.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/me")
@PreAuthorize("hasAnyRole('EMPLOYEE', 'GROUP_MANAGER')")
@RequiredArgsConstructor
public class UserProfileController {
    private final UserProfileService userProfileService;

    @PatchMapping("/nickname")
    @ResponseStatus(HttpStatus.OK)
    public UserNicknameResponseDto updateMyNickname(
            @AuthenticationPrincipal AppUserDetails currentUser,
            @Valid @RequestBody UpdateNicknameRequestDto request
    ) {
        return userProfileService.updateNickname(currentUser.getId(), request);
    }

    @PatchMapping("/password")
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204 — успех, тело не нужно
    public void updateMyPassword(
            @AuthenticationPrincipal AppUserDetails currentUser,
            @Valid @RequestBody UpdatePasswordRequestDto request
    ) {
        userProfileService.updatePassword(currentUser.getId(), request);
    }

}
