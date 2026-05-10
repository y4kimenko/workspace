package by.diplom.workspace.worker.controller.user;

import by.diplom.workspace.security.AppUserDetails;
import by.diplom.workspace.worker.dto.profile.request.UpdateNicknameRequestDto;
import by.diplom.workspace.worker.dto.profile.request.UpdatePasswordRequestDto;
import by.diplom.workspace.worker.dto.profile.request.UpdatePronounRequestDto;
import by.diplom.workspace.worker.dto.profile.response.UserNicknameResponseDto;
import by.diplom.workspace.worker.dto.profile.response.UserPronounResponseDto;
import by.diplom.workspace.worker.model.user.profile.Pronoun;
import by.diplom.workspace.worker.service.user.inter.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserProfileController {
    private final UserProfileService userProfileService;

    @PatchMapping("/me/nickname")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'GROUP_MANAGER')")
    @ResponseStatus(HttpStatus.OK)
    public UserNicknameResponseDto updateMyNickname(
            @AuthenticationPrincipal AppUserDetails currentUser,
            @Valid @RequestBody UpdateNicknameRequestDto request
    ) {
        return userProfileService.updateNickname(currentUser.getId(), request);
    }

    @PatchMapping("/me/password")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'GROUP_MANAGER')")
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204 — успех, тело не нужно
    public void updateMyPassword(
            @AuthenticationPrincipal AppUserDetails currentUser,
            @Valid @RequestBody UpdatePasswordRequestDto request
    ) {
        userProfileService.updatePassword(currentUser.getId(), request);
    }

    @PatchMapping("/me/pronoun")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'GROUP_MANAGER')")
    @ResponseStatus(HttpStatus.OK)
    public UserPronounResponseDto updateMyPronoun(
            @AuthenticationPrincipal AppUserDetails currentUser,
            @Valid @RequestBody UpdatePronounRequestDto request
    ) {
        return userProfileService.updatePronoun(currentUser.getId(), request);
    }

    @GetMapping("/pronouns")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'GROUP_MANAGER')")
    @ResponseStatus(HttpStatus.OK)
    public List<Map<String, String>> getAvailablePronouns() {
        return Arrays.stream(Pronoun.values())
                .map(p -> Map.of(
                        "value", p.name(),           // "HE_HIM"
                        "label", p.getDisplayName()  // "он/его"
                ))
                .toList();
    }
}
