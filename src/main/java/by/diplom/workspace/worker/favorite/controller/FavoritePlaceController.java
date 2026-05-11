package by.diplom.workspace.worker.favorite.controller;

import by.diplom.workspace.worker.favorite.service.FavoritePlaceService;
import by.diplom.workspace.place.dto.MeetingRoomResponseDto;
import by.diplom.workspace.place.dto.WorkplaceResponseDto;
import by.diplom.workspace.security.AppUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users/favorites")
@RequiredArgsConstructor
public class FavoritePlaceController {
    private final FavoritePlaceService favoritePlaceService;

    @GetMapping("/workplace")
    @PreAuthorize("hasAnyRole('GROUP_MANAGER', 'EMPLOYEE')")
    public List<WorkplaceResponseDto> getFavoritesWorkplace(@AuthenticationPrincipal AppUserDetails currentUser) {
        return favoritePlaceService.getFavoritesWorkplace(currentUser.getId());
    }

    @GetMapping("/meeting-room")
    @PreAuthorize("hasRole('GROUP_MANAGER')")
    public List<MeetingRoomResponseDto> getFavoritesMeetingRoom(@AuthenticationPrincipal AppUserDetails currentUser) {
        return favoritePlaceService.getFavoritesMeetingRoom(currentUser.getId());
    }

    @PostMapping("/{placeId}")
    @PreAuthorize("hasAnyRole('GROUP_MANAGER', 'EMPLOYEE')")
    @ResponseStatus(HttpStatus.CREATED)
    public void addToFavorites(
            @AuthenticationPrincipal AppUserDetails currentUser,
            @PathVariable Long placeId
    ) {
        favoritePlaceService.addToFavorites(currentUser.getId(), placeId);
    }

    @DeleteMapping("/{placeId}")
    @PreAuthorize("hasAnyRole('GROUP_MANAGER', 'EMPLOYEE')")
    public void removeFromFavorites(
            @AuthenticationPrincipal AppUserDetails currentUser,
            @PathVariable Long placeId
    ) {
        favoritePlaceService.removeFromFavorites(currentUser.getId(), placeId);
    }
}
