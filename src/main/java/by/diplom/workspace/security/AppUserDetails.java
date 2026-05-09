package by.diplom.workspace.security;

import by.diplom.workspace.worker.model.GroupManager;
import by.diplom.workspace.worker.model.user.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Getter
public class AppUserDetails implements UserDetails {

    private final UUID id;
    private final String username;
    private final String password;
    private final Role role;

    public AppUserDetails(User user) {
        this.id = user.getId();
        this.username = user.getNickname();
        this.password = user.getPasswordHash();
        // Определяем роль по типу сущности
        this.role = (user instanceof GroupManager)
                ? Role.ROLE_GROUP_MANAGER
                : Role.ROLE_EMPLOYEE;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}