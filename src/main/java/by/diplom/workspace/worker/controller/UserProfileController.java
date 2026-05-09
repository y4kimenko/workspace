package by.diplom.workspace.worker.controller;

import by.diplom.workspace.worker.dto.request.UpdatePasswordRequest;
import by.diplom.workspace.worker.dto.request.UpdatePronounRequest;
import by.diplom.workspace.worker.dto.response.UserPronounResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import by.diplom.workspace.authorization.AppUserDetails;
import by.diplom.workspace.worker.dto.request.UpdateNicknameRequest;
import by.diplom.workspace.worker.dto.response.UserNicknameResponse;
import by.diplom.workspace.worker.model.user.profile.Pronoun;
import by.diplom.workspace.worker.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public ResponseEntity<UserNicknameResponse> updateMyNickname(
            @AuthenticationPrincipal AppUserDetails currentUser,
            @Valid @RequestBody UpdateNicknameRequest request
    ) {
        UserNicknameResponse response = userProfileService.updateNickname(currentUser.getId(), request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/me/password")
    public ResponseEntity<Void> updateMyPassword(
            @AuthenticationPrincipal AppUserDetails currentUser,
            @Valid @RequestBody UpdatePasswordRequest request
    ) {
        userProfileService.updatePassword(currentUser.getId(), request);
        return ResponseEntity.noContent().build(); // 204 — успех, тело не нужно
    }

    @PatchMapping("/me/pronoun")
    public ResponseEntity<UserPronounResponse> updateMyPronoun(
            @AuthenticationPrincipal AppUserDetails currentUser,
            @Valid @RequestBody UpdatePronounRequest request
    ) {
        return ResponseEntity.ok(
                userProfileService.updatePronoun(currentUser.getId(), request)
        );
    }

    @GetMapping("/pronouns")
    public ResponseEntity<List<Map<String, String>>> getAvailablePronouns() {
        List<Map<String, String>> pronouns = Arrays.stream(Pronoun.values())
                .map(p -> Map.of(
                        "value", p.name(),           // "HE_HIM"
                        "label", p.getDisplayName()  // "он/его"
                ))
                .toList();
        return ResponseEntity.ok(pronouns);
    }
}
