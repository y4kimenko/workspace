package by.diplom.workspace.worker.worker.controller.user;


import by.diplom.workspace.security.AppUserDetails;
import by.diplom.workspace.worker.worker.dto.profile.request.UpdatePublicProfileRequestDto;
import by.diplom.workspace.worker.worker.dto.profile.response.PronounResponseDto;
import by.diplom.workspace.worker.worker.dto.profile.response.UserPartPublicProfileResponseDto;
import by.diplom.workspace.worker.worker.dto.profile.response.UserPublicProfileResponseDto;
import by.diplom.workspace.worker.worker.model.user.Pronoun;
import by.diplom.workspace.worker.worker.service.user.inter.UserProfileService;
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


@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasAnyRole('EMPLOYEE', 'GROUP_MANAGER')")
@RequiredArgsConstructor
public class UserPublicProfileController {
    private final UserProfileService userProfileService;

    @PatchMapping("/me/profile")
    @ResponseStatus(HttpStatus.OK)
    public UserPartPublicProfileResponseDto updateMyPublicProfile(
            @AuthenticationPrincipal AppUserDetails currentUser,
            @Valid @RequestBody UpdatePublicProfileRequestDto request
    ) {
        return userProfileService.updatePublicProfile(currentUser.getId(), request);
    }

    @GetMapping("/pronouns")
    @ResponseStatus(HttpStatus.OK)
    public List<PronounResponseDto> getAvailablePronouns() {
        return Arrays.stream(Pronoun.values())
                .map(p -> new PronounResponseDto(
                        p.name(),           // "HE_HIM"
                        p.getDisplayName()  // "он/его"
                ))
                .toList();
    }

    @GetMapping("/me/profile")
    @ResponseStatus(HttpStatus.OK)
    public UserPublicProfileResponseDto getMyProfile(@AuthenticationPrincipal AppUserDetails currentUser) {
        return userProfileService.getMyProfile(currentUser.getId());
    }

}
