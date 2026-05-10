package by.diplom.workspace.email.mapper;

import by.diplom.workspace.email.dto.response.UserEmailResponseDto;
import by.diplom.workspace.worker.model.user.profile.UserEmail;

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
