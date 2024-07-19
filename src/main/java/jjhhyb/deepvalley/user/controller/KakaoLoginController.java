package jjhhyb.deepvalley.user.controller;

import jjhhyb.deepvalley.user.dto.LoginResponseDto;
import jjhhyb.deepvalley.user.oauth.provider.kakao.KakaoTokenDto;
import jjhhyb.deepvalley.user.oauth.provider.kakao.KakaoUserInfoDto;
import jjhhyb.deepvalley.user.service.KakaoLoginService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/oauth")
public class KakaoLoginController {

    private final KakaoLoginService kakaoLoginService;

    public KakaoLoginController(KakaoLoginService kakaoLoginService) {
        this.kakaoLoginService = kakaoLoginService;
    }

    @GetMapping("/kakao")
    public ResponseEntity<LoginResponseDto> loginByKakao(@RequestParam("code") String code) throws Exception {
        return ResponseEntity.ok(kakaoLoginService.handleLogin(code));
    }

    // 테스트 목적
    @GetMapping("/kakao/token")
    public ResponseEntity<KakaoTokenDto> kakaoAccessToken(@RequestParam("code") String code) throws Exception {
        return ResponseEntity.ok(kakaoLoginService.getAccessToken(code));
    }

    // 테스트 목적
    @GetMapping("/kakao/info")
    public ResponseEntity<KakaoUserInfoDto> kakaoUserInfo(@RequestParam("token") String accessToken) throws Exception {
        return ResponseEntity.ok(kakaoLoginService.getUserInfo(accessToken));
    }
}
