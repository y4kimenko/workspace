package by.diplom.workspace.worker.email.mapper;

import by.diplom.workspace.worker.email.dto.response.UserEmailResponseDto;
import by.diplom.workspace.worker.email.model.UserEmail;

public class UserEmailMapper {
    public static UserEmailResponseDto toResponseDto(UserEmail e) {
        return new UserEmailResponseDto(
                e.getId(),
                e.getEmail(),
                e.isVerified(),
                e.isPrimaryEmail(),
                e.isPublicEmail()
        );
    }
}
