package by.diplom.workspace.admin.request_registration.service;

import by.diplom.workspace.admin.request_registration.dto.ApproveRegistrationRequestDto;
import by.diplom.workspace.admin.request_registration.dto.RejectRegistrationRequestDto;
import by.diplom.workspace.admin.request_registration.dto.RequestRegistrationResponseDto;
import by.diplom.workspace.worker.worker.dto.user.response.CreateUserResponseDto;

import java.util.List;


public interface RegistrationRequestAdminService {

    CreateUserResponseDto approve(Long registrationRequestId, ApproveRegistrationRequestDto requestDto);

    void reject(Long registrationRequestId, RejectRegistrationRequestDto requestDto);

    List<RequestRegistrationResponseDto> getAll();
}
