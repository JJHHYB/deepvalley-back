package jjhhyb.deepvalley.user.jwt;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomUserDetails implements UserDetails {

    private String loginId;

    public CustomUserDetails(String loginId) {
        this.loginId = loginId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // TODO: 사용자의 권한을 반환하도록 구현
        return null;
    }

    @Override
    public String getPassword() {
        // TODO: 사용자의 비밀번호를 반환하도록 구현
        return null;
    }

    @Override
    public String getUsername() {
        return loginId;
    }

    @Override
    public boolean isAccountNonExpired() {
        // TODO: 계정이 만료되지 않았는지 확인하는 로직을 구현
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // TODO: 계정이 잠기지 않았는지 확인하는 로직을 구현
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // TODO: 인증 정보가 만료되지 않았는지 확인하는 로직을 구현
        return true;
    }

    @Override
    public boolean isEnabled() {
        // TODO: 계정이 활성화되었는지 확인하는 로직을 구현
        return true;
    }
}