package by.diplom.workspace.security.controller;

import by.diplom.workspace.security.AppUserDetails;
import by.diplom.workspace.security.service.AppUserDetailsService;
import by.diplom.workspace.security.service.JwtService;
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

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.username());
        // Для in-memory admin UserDetails не является AppUserDetails,
        // поэтому генерируем токен чуть иначе
        String token;
        if (userDetails instanceof AppUserDetails appUser) {
            token = jwtService.generateToken(appUser);
        } else {
            token = jwtService.generateTokenForAdmin(userDetails);
        }
        return ResponseEntity.ok(new TokenResponse(token));
    }

    public record LoginRequest(String username, String password) {
    }

    public record TokenResponse(String token) {
    }
}
