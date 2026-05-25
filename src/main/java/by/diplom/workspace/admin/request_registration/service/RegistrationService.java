package by.diplom.workspace.admin.request_registration.service;

import by.diplom.workspace.admin.request_registration.dto.RegistrationRequestDto;
import by.diplom.workspace.worker.email.dto.request.VerifyEmailRequestDto;

public interface RegistrationService {

    void createRegistrationRequest(RegistrationRequestDto requestDto);

    void verifyEmail(VerifyEmailRequestDto requestDto);
}