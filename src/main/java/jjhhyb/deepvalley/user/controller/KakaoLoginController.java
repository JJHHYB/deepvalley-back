package jjhhyb.deepvalley.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import jjhhyb.deepvalley.user.dto.LoginResponseDto;
import jjhhyb.deepvalley.user.oauth.provider.kakao.KakaoTokenDto;
import jjhhyb.deepvalley.user.oauth.provider.kakao.KakaoUserInfoDto;
import jjhhyb.deepvalley.user.service.KakaoLoginService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jjhhyb.deepvalley.user.service.MemberService;

@RestController
@RequestMapping("/api/oauth")
@Tag(name = "Kakao Login", description = "카카오 로그인 API")
public class KakaoLoginController {

    private final KakaoLoginService kakaoLoginService;
    private final MemberService memberService;

    public KakaoLoginController(KakaoLoginService kakaoLoginService, MemberService memberService) {
        this.kakaoLoginService = kakaoLoginService;
        this.memberService = memberService;
    }

    @GetMapping("/kakao")
    @Operation(summary = "카카오 로그인", description = "카카오 코드를 사용하여 로그인하여 JWT토큰을 반환합니다.<br>" +
            "[카카오 엑세스 토큰 조회]와 [카카오 사용자 정보 조회]가 합쳐져있습니다.<br>" +
            "인증코드는 2회 이상 사용할 수 없습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "카카오 서버 오류", content = @Content(examples = @ExampleObject( value = "Kakao Server Error" )))
    })
    public ResponseEntity<LoginResponseDto> loginByKakao(
            @Parameter(description = "카카오 인증 코드", required = true) @RequestParam("code") String code) throws Exception {
        return ResponseEntity.ok(kakaoLoginService.handleLogin(code));
    }

    @GetMapping("/kakao/token")
    @Operation(summary = "[테스트용] 카카오 액세스 토큰 조회", description = "카카오 인증 코드를 사용하여 카카오 액세스 토큰을 조회합니다. 인증코드는 2회 이상 사용할 수 없습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토큰 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = KakaoTokenDto.class))),
            @ApiResponse(responseCode = "500", description = "카카오 서버 오류", content = @Content(examples = @ExampleObject( value = "Kakao Server Error" )))
    })
    public ResponseEntity<KakaoTokenDto> kakaoAccessToken(
            @Parameter(description = "카카오 인증 코드", required = true) @RequestParam("code") String code) throws Exception {
        return ResponseEntity.ok(kakaoLoginService.getAccessToken(code));
    }

    @GetMapping("/kakao/info")
    @Operation(summary = "[테스트용] 카카오 사용자 정보 조회", description = "카카오 액세스 토큰을 사용하여 카카오 사용자 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 정보 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = KakaoUserInfoDto.class))),
            @ApiResponse(responseCode = "500", description = "카카오 서버 오류", content = @Content(examples = @ExampleObject( value = "Kakao Server Error" )))
    })
    public ResponseEntity<KakaoUserInfoDto> kakaoUserInfo(
            @Parameter(description = "카카오 액세스 토큰", required = true) @RequestParam("token") String accessToken) throws Exception {
        return ResponseEntity.ok(kakaoLoginService.getUserInfo(accessToken));
    }

    @DeleteMapping("/kakao")
    @Operation(summary = "로그인한 카카오 계정정보 삭제", description = "카카오 계정정보를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "인증정보 삭제 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "카카오 서버 오류", content = @Content(examples = @ExampleObject( value = "Kakao Server Error" )))
    })
    public ResponseEntity<String> deleteKakaoMember(
            @Parameter(description = "카카오 액세스 토큰", required = true) Authentication auth) throws Exception {
        memberService.deleteMember(auth.getName());
        return ResponseEntity.ok().build();
    }
}
