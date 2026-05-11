package by.diplom.workspace.worker.email.controller;

import by.diplom.workspace.worker.email.dto.request.AddEmailRequestDto;
import by.diplom.workspace.worker.email.dto.request.UpdatePrimaryEmailRequestDto;
import by.diplom.workspace.worker.email.dto.request.VerifyEmailRequestDto;
import by.diplom.workspace.worker.email.dto.response.UserEmailResponseDto;
import by.diplom.workspace.worker.email.service.EmailVerificationService;
import by.diplom.workspace.security.AppUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
@RequestMapping("/api/users/me/emails")
@PreAuthorize("hasAnyRole('EMPLOYEE', 'GROUP_MANAGER')")
@RequiredArgsConstructor
public class EmailController {

    private final EmailVerificationService emailVerificationService;

    // добавить email и получить код
    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED) // 202 — письмо отправляется
    public void addEmail(
            @AuthenticationPrincipal AppUserDetails currentUser,
            @Valid @RequestBody AddEmailRequestDto request
    ) {
        emailVerificationService.addEmailAndSendCode(currentUser.getId(), request.email());
    }


    // подтвердить код
    @PostMapping("/verify")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void verifyEmail(
            @AuthenticationPrincipal AppUserDetails currentUser,
            @Valid @RequestBody VerifyEmailRequestDto request
    ) {
        emailVerificationService.verifyEmail(
                currentUser.getId(),
                request.email(),
                request.code()
        );
    }

    // повторная отправка кода
    @PostMapping("/resend")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void resendCode(
            @AuthenticationPrincipal AppUserDetails currentUser,
            @Valid @RequestBody AddEmailRequestDto request
    ) {
        emailVerificationService.resendCode(currentUser.getId(), request.email());
    }

    @GetMapping("/verify")
    @ResponseStatus(HttpStatus.OK)
    public List<UserEmailResponseDto> getVerifiedEmails(
            @AuthenticationPrincipal AppUserDetails currentUser
    ) {
        return emailVerificationService.getUserVerifiedEmails(currentUser.getId());
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserEmailResponseDto> getEmails(
            @AuthenticationPrincipal AppUserDetails currentUser
    ) {
        return emailVerificationService.getUserEmails(currentUser.getId());
    }

    @PatchMapping("/primary")
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204
    public void updatePrimaryEmail(
            @AuthenticationPrincipal AppUserDetails currentUser,
            @Valid @RequestBody UpdatePrimaryEmailRequestDto request
    ) {
        emailVerificationService.updatePrimaryEmail(
                currentUser.getId(),
                request.email()
        );
    }


    @DeleteMapping("/{email}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204
    public void deleteEmail(
            @AuthenticationPrincipal AppUserDetails currentUser,
            @PathVariable String email
    ) {
        emailVerificationService.deleteEmail(currentUser.getId(), email);
    }
}
