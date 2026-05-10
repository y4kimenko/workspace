package by.diplom.workspace.worker.controller.user;

import by.diplom.workspace.email.dto.request.UpdatePublicEmailRequestDto;
import by.diplom.workspace.email.service.EmailVerificationService;
import by.diplom.workspace.security.AppUserDetails;
import by.diplom.workspace.worker.dto.profile.request.UpdateBioRequestDto;
import by.diplom.workspace.worker.dto.profile.request.UpdatePronounRequestDto;
import by.diplom.workspace.worker.dto.profile.response.UserBioResponseDto;
import by.diplom.workspace.worker.dto.profile.response.UserPublicProfileResponseDto;
import by.diplom.workspace.worker.dto.profile.response.UserPronounResponseDto;
import by.diplom.workspace.socialLink.model.Pronoun;
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
@PreAuthorize("hasAnyRole('EMPLOYEE', 'GROUP_MANAGER')")
@RequiredArgsConstructor
public class UserPublicProfileController {
    private final UserProfileService userProfileService;
    private final EmailVerificationService emailVerificationService;

    @PatchMapping("/me/pronoun")
    @ResponseStatus(HttpStatus.OK)
    public UserPronounResponseDto updateMyPronoun(
            @AuthenticationPrincipal AppUserDetails currentUser,
            @Valid @RequestBody UpdatePronounRequestDto request
    ) {
        return userProfileService.updatePronoun(currentUser.getId(), request);
    }

    @GetMapping("/pronouns")
    @ResponseStatus(HttpStatus.OK)
    public List<Map<String, String>> getAvailablePronouns() {
        return Arrays.stream(Pronoun.values())
                .map(p -> Map.of(
                        "value", p.name(),           // "HE_HIM"
                        "label", p.getDisplayName()  // "он/его"
                ))
                .toList();
    }

    @PatchMapping("/emails/public")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePublicEmail(
            @AuthenticationPrincipal AppUserDetails currentUser,
            @Valid @RequestBody UpdatePublicEmailRequestDto request
    ) {
        emailVerificationService.updatePublicEmail(
                currentUser.getId(),
                request.email()
        );
    }

    @PatchMapping("/me/bio")
    @ResponseStatus(HttpStatus.OK)
    public UserBioResponseDto updateMyBio(
            @AuthenticationPrincipal AppUserDetails currentUser,
            @Valid @RequestBody UpdateBioRequestDto request
    ) {
        return userProfileService.updateBio(currentUser.getId(), request);
    }

    @GetMapping("/me/profile")
    @ResponseStatus(HttpStatus.OK)
    public UserPublicProfileResponseDto getMyProfile(@AuthenticationPrincipal AppUserDetails currentUser) {
        return userProfileService.getMyProfile(currentUser.getId());
    }

}
