package by.diplom.workspace.security.controller;

import by.diplom.workspace.security.AppUserDetails;
import by.diplom.workspace.security.RefreshToken;
import by.diplom.workspace.security.dto.LoginRequestDto;
import by.diplom.workspace.security.dto.RefreshRequestDto;
import by.diplom.workspace.security.dto.TokenResponseDto;
import by.diplom.workspace.security.service.AppUserDetailsService;
import by.diplom.workspace.security.service.JwtService;
import by.diplom.workspace.security.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final AppUserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@RequestBody LoginRequestDto request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.username());

        String accessToken;
        RefreshToken refreshToken;

        if (userDetails instanceof AppUserDetails appUser) {
            accessToken = jwtService.generateToken(appUser);
            refreshToken = refreshTokenService.createForUser(appUser.getId());
        } else {
            accessToken = jwtService.generateTokenForAdmin(userDetails);
            refreshToken = refreshTokenService.createForAdmin(userDetails.getUsername());
        }

        return ResponseEntity.ok(new TokenResponseDto(accessToken, refreshToken.getToken()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponseDto> refresh(@RequestBody RefreshRequestDto request) {
        RefreshToken old = refreshTokenService.rotateToken(request.refreshToken());

        String newAccessToken;
        RefreshToken newRefresh;

        if (old.getUserId() != null) {
            // Загружаем по id из БД
            UserDetails userDetails = userDetailsService.loadUserById(old.getUserId());
            AppUserDetails appUser = (AppUserDetails) userDetails;
            newAccessToken = jwtService.generateToken(appUser);
            newRefresh = refreshTokenService.createForUser(appUser.getId());
        } else {
            String adminUsername = old.getAdminUsername();
            UserDetails adminDetails = userDetailsService.loadUserByUsername(adminUsername);
            newAccessToken = jwtService.generateTokenForAdmin(adminDetails);
            newRefresh = refreshTokenService.createForAdmin(adminUsername);
        }

        return ResponseEntity.ok(new TokenResponseDto(newAccessToken, newRefresh.getToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody RefreshRequestDto request) {
        // При logout просто отзываем refresh token
        try {
            refreshTokenService.rotateToken(request.refreshToken());
        } catch (IllegalArgumentException ignored) {
            // Уже отозван — не проблема
        }
        return ResponseEntity.noContent().build();
    }
}