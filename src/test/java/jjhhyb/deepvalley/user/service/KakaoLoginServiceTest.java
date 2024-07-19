package jjhhyb.deepvalley.user.service;

import jjhhyb.deepvalley.user.oauth.provider.kakao.KakaoTokenDto;
import jjhhyb.deepvalley.user.oauth.provider.kakao.KakaoUserInfoDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class KakaoLoginServiceTest {

    @InjectMocks
    KakaoLoginService kakaoLoginService;

    @Mock
    RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("카카오 엑세스토큰 조회")
    void getAccessToken() throws Exception {
        // Given
        String authorizationCode = "test_code";
        KakaoTokenDto kakaoTokenDto = new KakaoTokenDto();
        when(restTemplate.postForEntity(anyString(), any(), eq(KakaoTokenDto.class))).thenReturn(new ResponseEntity<>(kakaoTokenDto, HttpStatus.OK));

        // When
        KakaoTokenDto result = kakaoLoginService.getAccessToken(authorizationCode);

        // Then
        assertNotNull(result);
        verify(restTemplate, times(1)).postForEntity(anyString(), any(), eq(KakaoTokenDto.class));
    }

    @Test
    @DisplayName("카카오 유저정보 조회")
    void getUserInfo() throws Exception {
        // Given
        String accessToken = "test_token";
        KakaoUserInfoDto kakaoUserInfoDto = new KakaoUserInfoDto();
        when(restTemplate.postForEntity(anyString(), any(), eq(KakaoUserInfoDto.class))).thenReturn(new ResponseEntity<>(kakaoUserInfoDto, HttpStatus.OK));

        // When
        KakaoUserInfoDto result = kakaoLoginService.getUserInfo(accessToken);

        // Then
        assertNotNull(result);
        verify(restTemplate, times(1)).postForEntity(anyString(), any(), eq(KakaoUserInfoDto.class));
    }
}