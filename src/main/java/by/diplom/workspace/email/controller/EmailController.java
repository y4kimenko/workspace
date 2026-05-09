package by.diplom.workspace.email.controller;

import by.diplom.workspace.email.dto.request.AddEmailRequest;
import by.diplom.workspace.email.dto.request.UpdatePrimaryEmailRequest;
import by.diplom.workspace.email.dto.request.UpdatePublicEmailRequest;
import by.diplom.workspace.email.dto.request.VerifyEmailRequest;
import by.diplom.workspace.email.dto.response.UserEmailResponse;
import by.diplom.workspace.email.service.EmailVerificationService;
import by.diplom.workspace.security.AppUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@Validated
@RequestMapping("/api/users/me/emails")
@RequiredArgsConstructor
public class EmailController {

    private final EmailVerificationService emailVerificationService;

    // добавить email и получить код
    @PostMapping
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'GROUP_MANAGER')")
    @ResponseStatus(HttpStatus.ACCEPTED) // 202 — письмо отправляется
    public void addEmail(
            @AuthenticationPrincipal AppUserDetails currentUser,
            @Valid @RequestBody AddEmailRequest request
    ) {
        emailVerificationService.addEmailAndSendCode(currentUser.getId(), request.email());
    }


    // подтвердить код
    @PostMapping("/verify")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'GROUP_MANAGER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void verifyEmail(
            @AuthenticationPrincipal AppUserDetails currentUser,
            @Valid @RequestBody VerifyEmailRequest request
    ) {
        emailVerificationService.verifyEmail(
                currentUser.getId(),
                request.email(),
                request.code()
        );
    }

    // повторная отправка кода
    @PostMapping("/resend")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'GROUP_MANAGER')")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void resendCode(
            @AuthenticationPrincipal AppUserDetails currentUser,
            @Valid @RequestBody AddEmailRequest request
    ) {
        emailVerificationService.resendCode(currentUser.getId(), request.email());
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'GROUP_MANAGER')")
    @ResponseStatus(HttpStatus.OK)
    public List<UserEmailResponse> getMyEmails(
            @AuthenticationPrincipal AppUserDetails currentUser
    ) {
        return emailVerificationService.getUserEmails(currentUser.getId());
    }

    @PatchMapping("/primary")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'GROUP_MANAGER')")
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204
    public void updatePrimaryEmail(
            @AuthenticationPrincipal AppUserDetails currentUser,
            @Valid @RequestBody UpdatePrimaryEmailRequest request
    ) {
        emailVerificationService.updatePrimaryEmail(
                currentUser.getId(),
                request.email()
        );
    }

    @PatchMapping("/public")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'GROUP_MANAGER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePublicEmail(
            @AuthenticationPrincipal AppUserDetails currentUser,
            @Valid @RequestBody UpdatePublicEmailRequest request
    ) {
        emailVerificationService.updatePublicEmail(
                currentUser.getId(),
                request.email()
        );
    }

    @DeleteMapping("/{email}")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'GROUP_MANAGER')")
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204
    public void deleteEmail(
            @AuthenticationPrincipal AppUserDetails currentUser,
            @PathVariable String email
    ) {
        emailVerificationService.deleteEmail(currentUser.getId(), email);
    }
}
