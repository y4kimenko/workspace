package by.diplom.workspace.security.service;


import by.diplom.workspace.security.AppUserDetails;
import by.diplom.workspace.security.Role;
import by.diplom.workspace.worker.model.user.User;
import by.diplom.workspace.worker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.password-hash}")
    private String adminPasswordHash;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        // In-memory admin — проверяем только по нику, у него нет email
        if (adminUsername.equals(usernameOrEmail)) {
            return new org.springframework.security.core.userdetails.User(
                    adminUsername,
                    adminPasswordHash,
                    List.of(new SimpleGrantedAuthority(Role.ROLE_ADMIN.name()))
            );
        }

        // Ищем по nickname
        Optional<User> found = userRepository.findByNickname(usernameOrEmail);

        // Если не нашли по нику — ищем по verified email
        if (found.isEmpty()) {
            found = userRepository.findByVerifiedEmail(usernameOrEmail);
        }

        return found
                .map(AppUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found by nickname or email: " + usernameOrEmail
                ));
    }

    public UserDetails loadUserById(UUID id) {
        return userRepository.findById(id)
                .map(AppUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found by id: " + id));
    }
}
