package jjhhyb.deepvalley.user.controller;

import jjhhyb.deepvalley.user.auth.JWTUtil;
import jjhhyb.deepvalley.user.dto.*;
import jjhhyb.deepvalley.user.entity.Member;
import jjhhyb.deepvalley.user.exception.LoginException;
import jjhhyb.deepvalley.user.exception.RegisterException;
import jjhhyb.deepvalley.user.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/member")
public class MemberController {
    private final MemberService memberService;
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

//    @Tag(name = "Member", description = "새로운 회원 등록")
    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDto> register(@RequestBody RegisterRequestDto registerRequestDto) throws Exception {
        // Entity에 회원 등록
        Member member = new Member();
        member.setLoginEmail(registerRequestDto.getLoginEmail());
        member.setName(registerRequestDto.getName());
        member.setPassword(registerRequestDto.getPassword());

        // 회원 등록
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

//    @Tag(name = "Member", description = "회원 로그인")
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        Optional<Member> user = memberService.authenticate(loginRequestDto.getLoginEmail(), loginRequestDto.getPassword());

        if (user.isEmpty()) {
            throw new LoginException.UserNotFoundException("User not found");
        }

        if (!user.get().getPassword().equals(loginRequestDto.getPassword())) {
            throw new LoginException.InvalidCredentialsException("Invalid password");
        }

        // Update loginDate
        Member memberEntity = user.get();
        memberEntity.setLoginDate(LocalDateTime.now());
        memberService.save(memberEntity);

        String token = JWTUtil.createJwt(user.get().getLoginEmail(), "USER", 60*60*1000L);
        return ResponseEntity.ok(new LoginResponseDto(token));
    }

    //    @Tag(name = "Member", description = "내 정보 조회")
    @GetMapping
    public ResponseEntity<ProfileResponseDto> getMyinfo(Authentication auth) {
        Optional<Member> member = memberService.getMemberByLoginEmail(auth.getName());
        return member.map(m -> ResponseEntity.ok(ProfileResponseDto.fromMember(m)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    //    @Tag(name = "Member", description = "회원 프로필 수정")
    @PutMapping
    public ResponseEntity<ProfileResponseDto> updateMember(@RequestBody ProfileRequestDto profileRequestDto, Authentication auth) throws Exception {
        Optional<Member> member = memberService.updateMember(profileRequestDto, auth.getName());
        return member.map(m -> ResponseEntity.ok(ProfileResponseDto.fromMember(m)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    //    @Tag(name = "Member", description = "비밀번호 변경")
    @PutMapping("/change-password")
    public ResponseEntity<Void> changePassword(@RequestBody PasswordRequestDto passwordRequestDto, Authentication auth) {
        memberService.changePassword(passwordRequestDto, auth.getName());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
//    @Tag(name = "Member", description = "회원 탈퇴")
    public ResponseEntity<Void> deleteMember(@RequestBody LoginRequestDto loginRequestDto, Authentication auth) {
        memberService.deleteMember(loginRequestDto, auth.getName());
        return ResponseEntity.ok().build();
    }
}
