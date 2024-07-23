package jjhhyb.deepvalley.user.auth;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

// JWT 토큰을 생성하고 검증하는 유틸리티 클래스
@Component
public class JWTUtil {
    // JWT 토큰을 생성하고 검증하는데 사용되는 비밀 키
    private static SecretKey secretKey = null;
    // JWT 토큰의 만료 시간 (밀리초)
    private static Long expiredMs = 0L;

    // 생성자. 비밀 키와 만료 시간을 초기화함
    public JWTUtil(@Value("${jwt.secretkey}") String secret, @Value("${jwt.expiretime}") Long expired) {
        // 비밀 키를 바이트 배열로 변환하고, 이를 이용해 SecretKey 객체를 생성함
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
        expiredMs = expired;
    }

    // 토큰에서 로그인 ID를 추출함
    public String getLoginId(String token) {
        return getClaim(token, "loginId");
    }

    // 토큰에서 역할을 추출함
    public String getRole(String token) {
        return getClaim(token, "role");
    }

    // 토큰이 만료되었는지 확인함
    public Boolean isExpired(String token) {
        Claims claims = getClaims(token);
        return claims.getExpiration().before(new Date());
    }

    // JWT 토큰을 생성함
    public static String createJwt(String loginId, String role, long l) {
        return Jwts.builder()
                .claim("loginId", loginId)  // 로그인 ID를 claim으로 추가함
                .claim("role", role)  // 역할을 claim으로 추가함
                .issuedAt(new Date(System.currentTimeMillis()))  // 발행 시간을 설정함
                .expiration(new Date(System.currentTimeMillis() + expiredMs))  // 만료 시간을 설정함
                .signWith(secretKey)  // 비밀 키로 서명함
                .compact();  // JWT 토큰을 문자열로 변환함
    }

    // 토큰에서 Claims를 추출함
    private Claims getClaims(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
    }

    // 토큰에서 특정 claim을 추출함
    private String getClaim(String token, String name) {
        return getClaims(token).get(name, String.class);
    }
}