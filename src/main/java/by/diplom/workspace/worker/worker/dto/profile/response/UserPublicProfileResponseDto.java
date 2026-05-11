package by.diplom.workspace.worker.worker.dto.profile.response;


public record UserPublicProfileResponseDto(
        String fullName,
        String nickname,
        String avatarPath,
        String bio,
        PronounResponseDto pronoun,

        DepartmentPositionInfo departmentPosition,
        String publicEmail
) {
    public record DepartmentPositionInfo(
            String positionName,
            String departmentName
    ) {
    }
}