package jjhhyb.deepvalley.user.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collection;

// 필요한 필드를 final로 선언하고 생성자를 통해 초기화하는 Lombok 어노테이션
@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    // 인증을 처리하는 객체
    private final AuthenticationManager authenticationManager;
    // JWT 토큰을 생성하고 검증하는 유틸리티 클래스
    private final JWTUtil jwtUtil;

    // 로그인 시도 시 호출되는 메소드
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {

        // 요청에서 로그인 ID와 비밀번호를 추출함
        String loginId = obtainUsername(request);
        String password = obtainPassword(request);

        // 로그인 ID와 비밀번호를 이용해 UsernamePasswordAuthenticationToken 객체를 생성함
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(loginId, password);

        // AuthenticationManager에게 인증을 요청함
        return authenticationManager.authenticate(authToken);
    }

    // 로그인 성공 시 호출되는 메소드
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {
        // 인증 정보에서 username을 추출함
        String username = ((CustomUserDetails) authentication.getPrincipal()).getUsername();

        // 인증 정보에서 role을 추출함
        String role = ((Collection<GrantedAuthority>) authentication.getAuthorities()).iterator().next().getAuthority();

        // JWTUtil에게 JWT 토큰 생성을 요청함
        String token = jwtUtil.createJwt(username, role, 60*60*1000L);

        // JWT 토큰을 응답 헤더에 추가함
        response.addHeader("Authorization", "Bearer " + token);
    }

    // 로그인 실패 시 호출되는 메소드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException {

        // 실패 시 401 응답코드를 보냄
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        // 실패 이유를 응답 본문에 쓰기
        response.getWriter().write("Authentication failed: " + failed.getMessage());
    }
}