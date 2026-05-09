package by.diplom.workspace.email.controller;

import by.diplom.workspace.authorization.AppUserDetails;
import by.diplom.workspace.email.dto.AddEmailRequest;
import by.diplom.workspace.email.dto.VerifyEmailRequest;
import by.diplom.workspace.email.service.EmailVerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/users/me/emails")
@RequiredArgsConstructor
public class EmailController {

    private final EmailVerificationService emailVerificationService;

    // добавить email и получить код
    @PostMapping
    public ResponseEntity<Void> addEmail(
            @AuthenticationPrincipal AppUserDetails currentUser,
            @Valid @RequestBody AddEmailRequest request
    ) {
        emailVerificationService.addEmailAndSendCode(currentUser.getId(), request.email());
        return ResponseEntity.accepted().build(); // 202 — письмо отправляется
    }

    // подтвердить код
    @PostMapping("/verify")
    public ResponseEntity<Void> verifyEmail(
            @AuthenticationPrincipal AppUserDetails currentUser,
            @Valid @RequestBody VerifyEmailRequest request
    ) {
        emailVerificationService.verifyEmail(
                currentUser.getId(),
                request.email(),
                request.code()
        );
        return ResponseEntity.noContent().build(); // 204 — успешно подтверждено
    }

    // повторная отправка кода
    @PostMapping("/resend")
    public ResponseEntity<Void> resendCode(
            @AuthenticationPrincipal AppUserDetails currentUser,
            @Valid @RequestBody AddEmailRequest request
    ) {
        emailVerificationService.resendCode(currentUser.getId(), request.email());
        return ResponseEntity.accepted().build();
    }
}
