package by.diplom.workspace.worker.dto.profile.response;


public record UserPublicProfileResponseDto(
        String fullName,
        String nickname,
        String avatarPath,
        String bio,
        String pronoun,

        DepartmentPositionInfo departmentPosition,
        String publicEmail
) {
    public record DepartmentPositionInfo(
            String positionName,
            String departmentName
    ) {}
}