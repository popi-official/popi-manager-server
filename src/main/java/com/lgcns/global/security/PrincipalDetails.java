package com.lgcns.global.security;

import com.lgcns.domain.manager.domain.Manager;
import com.lgcns.domain.manager.domain.ManagerRole;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@AllArgsConstructor
public class PrincipalDetails implements UserDetails {

    private Manager manager;
    private Map<String, Object> attributes;

    private PrincipalDetails(Manager manager) {
        this.manager = manager;
    }

    public static PrincipalDetails from(Manager manager) {
        return new PrincipalDetails(manager);
    }

    public Long getId() {
        return manager.getId();
    }

    public ManagerRole getManagerRole() {
        return manager.getRole();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(manager.getRole().getRoleName()));
    }

    @Override
    public String getPassword() {
        return manager.getPassword();
    }

    @Override
    public String getUsername() {
        return manager.getUsername();
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
