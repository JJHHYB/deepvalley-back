package jjhhyb.deepvalley.config;

import jjhhyb.deepvalley.user.auth.JWTFilter;
import jjhhyb.deepvalley.user.auth.JWTUtil;
import jjhhyb.deepvalley.user.auth.LoginFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration configuration;
    private final JWTUtil jwtUtil;

    // AuthenticationManager Bean을 생성하는 메소드
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    // SecurityFilterChain Bean을 생성하는 메소드
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)                                                 // CSRF 보호를 비활성화
                .formLogin(AbstractHttpConfigurer::disable)                                            // 폼 기반 로그인을 비활성화
                .httpBasic((AbstractHttpConfigurer::disable))                                          // HTTP Basic 인증을 비활성화
                .authorizeHttpRequests((auth) -> auth                                                  // 요청별 권한 설정
                        .requestMatchers("/api/member/register", "/api/member/login").permitAll()  // 특정 경로는 모든 사용자에게 허용
                        .requestMatchers("/jwt-login/admin").hasRole("ADMIN")                        // 특정 경로는 ADMIN 역할을 가진 사용자에게만 허용
                        .anyRequest().authenticated()                                                  // 그 외의 모든 요청은 인증된 사용자에게만 허용
                )
                .sessionManagement((session) -> session  // 세션 관리 설정
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // 세션을 생성하지 않고, JWT를 사용하여 인증
                .addFilterAt(new LoginFilter(authenticationManager(configuration), jwtUtil), UsernamePasswordAuthenticationFilter.class)  // 로그인 필터 추가
                .addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class);      // JWT 필터를 로그인 필터 이전에 추가

        return http.build();
    }

    // BCryptPasswordEncoder Bean을 생성하는 메소드
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    // CorsFilter Bean을 생성하는 메소드
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*");   //config.addAllowedOrigin("*");  addAllowedOrigin는 특정 도메인만 가능, * 사용불가
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}