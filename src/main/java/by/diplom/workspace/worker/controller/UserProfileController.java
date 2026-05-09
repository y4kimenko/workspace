package by.diplom.workspace.worker.controller;

import by.diplom.workspace.worker.model.user.profile.Pronoun;
import by.diplom.workspace.worker.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
