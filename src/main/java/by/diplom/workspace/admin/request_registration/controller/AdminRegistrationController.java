package by.diplom.workspace.admin.request_registration.controller;

import by.diplom.workspace.admin.request_registration.dto.ApproveRegistrationRequestDto;
import by.diplom.workspace.admin.request_registration.dto.RejectRegistrationRequestDto;
import by.diplom.workspace.admin.request_registration.dto.RequestRegistrationResponseDto;
import by.diplom.workspace.admin.request_registration.service.RegistrationRequestAdminService;
import by.diplom.workspace.admin.users.dto.response.CreateUserResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/registration-requests")
public class AdminRegistrationController {

    private final RegistrationRequestAdminService registrationRequestAdminService;

    @PostMapping("/{registrationRequestId}/approve")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateUserResponseDto approve(
            @PathVariable Long registrationRequestId,
            @Valid @RequestBody ApproveRegistrationRequestDto requestDto
    ) {
        return registrationRequestAdminService.approve(registrationRequestId, requestDto);
    }

    @PostMapping("/{registrationRequestId}/reject")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reject(
            @PathVariable Long registrationRequestId,
            @Valid @RequestBody RejectRegistrationRequestDto requestDto
    ) {
        registrationRequestAdminService.reject(registrationRequestId, requestDto);
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<RequestRegistrationResponseDto> getAllRegistrationRequests() {
        return registrationRequestAdminService.getAll();
    }
}
