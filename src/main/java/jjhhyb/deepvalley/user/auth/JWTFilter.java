package jjhhyb.deepvalley.user.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

// 필요한 필드를 final로 선언하고 생성자를 통해 초기화하는 Lombok 어노테이션
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    // JWT 처리를 위한 유틸리티 클래스
    private final JWTUtil jwtUtil;
    // 로깅을 위한 SLF4J 로거
    private static final Logger logger = LoggerFactory.getLogger(JWTFilter.class);

    // HTTP 요청이 들어올 때마다 실행되는 메소드
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException, ServletException {
        // 요청 헤더에서 "Authorization" 키의 값을 가져옴
        String authorization = request.getHeader("Authorization");

        // 토큰이 없거나 Bearer로 시작하지 않으면 로그를 남기고 다음 필터로 넘어감
        if(authorization == null || !authorization.startsWith("Bearer ")){
            logger.debug("Token is null");
            filterChain.doFilter(request, response);
            return;
        }

        // 토큰을 추출함 (Bearer 다음의 문자열)
        String token = authorization.split(" ")[1];

        // 토큰이 만료되었으면 로그를 남기고 다음 필터로 넘어감
        if(jwtUtil.isExpired(token)){
            logger.debug("Token is expired");
            filterChain.doFilter(request, response);
            return;
        }

        // 토큰에서 로그인 ID을 추출함
        String loginId = jwtUtil.getLoginId(token);

        // CustomUserDetails 객체를 생성함
        CustomUserDetails customUserDetails = new CustomUserDetails(loginId);

        // 인증 토큰을 생성하고 SecurityContext에 설정함
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // 다음 필터로 요청과 응답을 넘김
        filterChain.doFilter(request, response);
    }
}