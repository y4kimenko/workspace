package by.diplom.workspace.admin.request_registration.controller;


import by.diplom.workspace.admin.request_registration.dto.RegistrationRequestDto;
import by.diplom.workspace.admin.request_registration.service.RegistrationService;
import by.diplom.workspace.worker.email.dto.request.VerifyEmailRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/registration")
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void createRegistrationRequest(
            @Valid @RequestBody RegistrationRequestDto requestDto
    ) {
        registrationService.createRegistrationRequest(requestDto);
    }

    // подтвердить код
    @PostMapping("/verify")
    public void verifyEmail(
            @Valid @RequestBody VerifyEmailRequestDto request
    ) {
        registrationService.verifyEmail(request);
    }
}