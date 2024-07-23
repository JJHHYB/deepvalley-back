package jjhhyb.deepvalley.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import jjhhyb.deepvalley.user.jwt.JWTUtil;
import jjhhyb.deepvalley.user.dto.*;
import jjhhyb.deepvalley.user.entity.Member;
import jjhhyb.deepvalley.user.exception.LoginException;
import jjhhyb.deepvalley.user.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/member")
@Tag(name = "Member", description = "회원 관리 API")
public class MemberController {
    private final MemberService memberService;
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/register")
    @Operation(summary = "회원 등록", description = "회원을 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원 등록 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RegisterResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "중복된 값", content = @Content(examples = {
                    @ExampleObject(name = "이메일 중복", value = "Login email already in use"),
                    @ExampleObject(name = "이름(닉네임) 중복", value = "Name already in use")
            })),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(examples = @ExampleObject( value = "Bad Request" ))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(examples = @ExampleObject( value = "Internal Server Error" )))
    })
    public ResponseEntity<RegisterResponseDto> register(
            @Parameter(description = "회원 등록 정보", required = true) @RequestBody RegisterRequestDto registerRequestDto) throws Exception {
        Member member = new Member();
        member.setLoginEmail(registerRequestDto.getLoginEmail());
        member.setName(registerRequestDto.getName());
        member.setPassword(registerRequestDto.getPassword());

        Member registeredMember = memberService.register(member);
        RegisterResponseDto responseDto = RegisterResponseDto.builder()
                .memberId(registeredMember.getMemberId())
                .loginEmail(registeredMember.getLoginEmail())
                .name(registeredMember.getName())
                .password(registeredMember.getPassword())
                .createdDate(registeredMember.getCreatedDate())
                .build();
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "로그인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "잘못된 자격 증명", content = @Content(examples = @ExampleObject( value = "Invalid password" ))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content(examples = @ExampleObject( value = "User not found" ))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(examples = @ExampleObject( value = "Bad Request" ))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(examples = @ExampleObject( value = "Internal Server Error" )))
    })
    public ResponseEntity<LoginResponseDto> login(
            @Parameter(description = "로그인 정보", required = true) @RequestBody LoginRequestDto loginRequestDto) {
        Optional<Member> user = memberService.authenticate(loginRequestDto.getLoginEmail(), loginRequestDto.getPassword());

        if (user.isEmpty()) {
            throw new LoginException.UserNotFoundException("User not found");
        }

        if (!user.get().getPassword().equals(loginRequestDto.getPassword())) {
            throw new LoginException.InvalidCredentialsException("Invalid password");
        }

        Member memberEntity = user.get();
        memberEntity.setLoginDate(LocalDateTime.now());
        memberService.save(memberEntity);

        String token = JWTUtil.createJwt(user.get().getLoginEmail(), "USER", 60*60*1000L);
        return ResponseEntity.ok(new LoginResponseDto(token));
    }

    @GetMapping
    @Operation(summary = "내 정보 조회", description = "현재 로그인한 사용자의 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProfileResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content(examples = @ExampleObject( value = "User not found" ))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(examples = @ExampleObject( value = "Bad Request" ))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(examples = @ExampleObject( value = "Internal Server Error" )))
    })
    public ResponseEntity<ProfileResponseDto> getMyinfo(
            @Parameter(hidden = true) Authentication auth,
            @Parameter(description = "JWT 토큰", required = true, example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
            @RequestHeader("Authorization") String authorization) {
        Optional<Member> member = memberService.getMemberByLoginEmail(auth.getName());
        return member.map(m -> ResponseEntity.ok(ProfileResponseDto.fromMember(m)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping
    @Operation(summary = "회원 프로필 수정", description = "현재 로그인한 사용자의 프로필을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProfileResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content(examples = @ExampleObject( value = "User not found" ))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(examples = @ExampleObject( value = "Bad Request" ))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(examples = @ExampleObject( value = "Internal Server Error" )))
    })
    public ResponseEntity<ProfileResponseDto> updateMember(
            @Parameter(description = "프로필 수정 정보", required = true) @RequestBody ProfileRequestDto profileRequestDto,
            Authentication auth) throws Exception {
        Optional<Member> member = memberService.updateMember(profileRequestDto, auth.getName());
        return member.map(m -> ResponseEntity.ok(ProfileResponseDto.fromMember(m)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/change-password")
    @Operation(summary = "비밀번호 변경", description = "현재 로그인한 사용자의 비밀번호를 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비밀번호 변경 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(examples = @ExampleObject( value = "Bad Request" ))),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 현재 비밀번호", content = @Content(examples = @ExampleObject( value = "Invalid Old Password" ))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content(examples = @ExampleObject( value = "User not found" ))),
            @ApiResponse(responseCode = "422", description = "서로 같은 비밀번호 설정", content = @Content(examples = @ExampleObject( value = "New password is the same as Old password" ))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(examples = @ExampleObject( value = "Internal Server Error" )))
    })
    public ResponseEntity<Void> changePassword(
            @Parameter(description = "비밀번호 변경 정보", required = true) @RequestBody PasswordRequestDto passwordRequestDto, Authentication auth) {
        memberService.changePassword(passwordRequestDto, auth.getName());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    @Operation(summary = "회원 탈퇴", description = "현재 로그인한 사용자의 계정을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 탈퇴 성공"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content(examples = @ExampleObject( value = "User not found" ))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(examples = @ExampleObject( value = "Bad Request" ))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(examples = @ExampleObject( value = "Internal Server Error" )))
    })
    public ResponseEntity<Void> deleteMember(
            @Parameter(description = "회원 탈퇴 정보", required = true) @RequestBody LoginRequestDto loginRequestDto,
            Authentication auth) {
        memberService.deleteMember(loginRequestDto, auth.getName());
        return ResponseEntity.ok().build();
    }
}
