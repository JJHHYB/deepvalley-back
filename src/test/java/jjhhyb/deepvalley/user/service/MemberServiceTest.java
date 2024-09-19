package jjhhyb.deepvalley.user.service;

import static org.junit.jupiter.api.Assertions.*;
import jjhhyb.deepvalley.user.dto.LoginRequestDto;
import jjhhyb.deepvalley.user.dto.PasswordRequestDto;
import jjhhyb.deepvalley.user.dto.ProfileRequestDto;
import jjhhyb.deepvalley.user.entity.Member;
import jjhhyb.deepvalley.user.exception.MyProfileException;
import jjhhyb.deepvalley.user.exception.RegisterException;
import jjhhyb.deepvalley.user.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import static org.mockito.Mockito.*;

@SpringBootTest
public class MemberServiceTest {
    @Autowired
    private MemberService memberService;
    @MockBean
    private MemberRepository memberRepository;
    private Member member;

    // 각 테스트 전에 Member 객체를 초기화하는 메소드
    @BeforeEach
    public void setUp() {
        member = new Member();
        member.setLoginEmail("test@test.com");
        member.setName("test");
        member.setPassword("test");
    }

    // MemberService의 register 메소드에 대한 테스트
    @Test
    @DisplayName("회원 등록")
    public void registerTest() throws RegisterException {
        // memberRepository 메소드의 동작을 모킹
        when(memberRepository.findByLoginEmail(member.getLoginEmail())).thenReturn(Optional.empty());
        when(memberRepository.findByName(member.getName())).thenReturn(Optional.empty());
        when(memberRepository.save(member)).thenReturn(member);

        // 테스트할 메소드 호출
        Member registeredMember = memberService.register(member);

        // 예상된 결과를 검증
        assertEquals(member, registeredMember);
        verify(memberRepository, times(1)).findByLoginEmail(member.getLoginEmail());
        verify(memberRepository, times(1)).findByName(member.getName());
        verify(memberRepository, times(1)).save(member);
    }

    // MemberService의 authenticate 메소드에 대한 테스트
    @Test
    @DisplayName("회원 로그인")
    public void authenticateTest() {
        // memberRepository 메소드의 동작을 모킹
        when(memberRepository.findByLoginEmailAndPassword(member.getLoginEmail(), member.getPassword())).thenReturn(Optional.of(member));

        // 테스트할 메소드 호출
        Optional<Member> authenticatedMember = memberService.authenticate(member.getLoginEmail(), member.getPassword());

        // 예상된 결과를 검증
        assertTrue(authenticatedMember.isPresent());
        assertEquals(member, authenticatedMember.get());
        verify(memberRepository, times(1)).findByLoginEmailAndPassword(member.getLoginEmail(), member.getPassword());
    }

    // MemberService의 updateMember 메소드에 대한 테스트
    @Test
    @DisplayName("회원 정보 수정")
    public void updateMemberTest() throws Exception {
        // 테스트에 사용할 ProfileRequestDto 객체 생성
        ProfileRequestDto profileRequestDto = new ProfileRequestDto();
        profileRequestDto.setName("updatedName");
        profileRequestDto.setProfileImageUrl("updatedImageUrl");
        profileRequestDto.setDescription("updatedDescription");

        // memberRepository 메소드의 동작을 모킹
        when(memberRepository.findByLoginEmail(member.getLoginEmail())).thenReturn(Optional.of(member));
        when(memberRepository.findByName(profileRequestDto.getName())).thenReturn(Optional.empty());
        when(memberRepository.save(member)).thenReturn(member);

        // 테스트할 메소드 호출
        Optional<Member> updatedMember = memberService.updateMember(profileRequestDto, null, member.getLoginEmail());

        // 예상된 결과를 검증
        assertTrue(updatedMember.isPresent());
        assertEquals(profileRequestDto.getName(), updatedMember.get().getName());
        assertEquals(profileRequestDto.getProfileImageUrl(), updatedMember.get().getProfileImageUrl());
        assertEquals(profileRequestDto.getDescription(), updatedMember.get().getDescription());
        verify(memberRepository, times(1)).findByLoginEmail(member.getLoginEmail());
        verify(memberRepository, times(1)).findByName(profileRequestDto.getName());
        verify(memberRepository, times(1)).save(member);
    }

    // MemberService의 changePassword 메소드에 대한 테스트
    @Test
    @DisplayName("회원 비밀번호 변경")
    public void changePasswordTest() throws MyProfileException {
        // 테스트에 사용할 PasswordRequestDto 객체 생성
        PasswordRequestDto passwordRequestDto = new PasswordRequestDto();
        //TODO 메서드명 변경
//        passwordRequestDto.setPassword("updatedPassword");

        // memberRepository 메소드의 동작을 모킹
        when(memberRepository.findByLoginEmail(member.getLoginEmail())).thenReturn(Optional.of(member));
        when(memberRepository.save(member)).thenReturn(member);

        // 테스트할 메소드 호출
        memberService.changePassword(passwordRequestDto, member.getLoginEmail());

        // 예상된 결과를 검증
        verify(memberRepository, times(1)).findByLoginEmail(member.getLoginEmail());
        verify(memberRepository, times(1)).save(member);
    }

    // MemberService의 deleteMember 메소드에 대한 테스트
    @Test
    @DisplayName("회원 삭제")
    public void deleteMemberTest() throws MyProfileException {
        // 테스트에 사용할 LoginRequestDto 객체 생성
        LoginRequestDto loginRequestDto = new LoginRequestDto();
        loginRequestDto.setLoginEmail(member.getLoginEmail());

        // memberRepository 메소드의 동작을 모킹
        when(memberRepository.findByLoginEmail(member.getLoginEmail())).thenReturn(Optional.of(member));

        // 테스트할 메소드 호출
        memberService.deleteMember(loginRequestDto, member.getLoginEmail());

        // 예상된 결과를 검증
        verify(memberRepository, times(1)).findByLoginEmail(member.getLoginEmail());
        verify(memberRepository, times(1)).delete(member);
    }

    // MemberService의 save 메소드에 대한 테스트
    @Test
    @DisplayName("회원 저장")
    public void saveTest() {
        // memberRepository 메소드의 동작을 모킹
        when(memberRepository.save(member)).thenReturn(member);

        // 테스트할 메소드 호출
        memberService.save(member);

        // 예상된 결과를 검증
        verify(memberRepository, times(1)).save(member);
    }

}