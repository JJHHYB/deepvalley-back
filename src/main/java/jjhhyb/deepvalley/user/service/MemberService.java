package jjhhyb.deepvalley.user.service;

import jjhhyb.deepvalley.user.dto.LoginRequestDto;
import jjhhyb.deepvalley.user.dto.PasswordRequestDto;
import jjhhyb.deepvalley.user.dto.ProfileRequestDto;
import jjhhyb.deepvalley.user.entity.Member;
import jjhhyb.deepvalley.user.exception.MyProfileException;
import jjhhyb.deepvalley.user.exception.RegisterException;
import jjhhyb.deepvalley.user.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
public class MemberService {
    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional
    public Member register(Member member) throws RegisterException {
        // 비어있는 필드 확인
        if (member.getLoginEmail() == null || member.getLoginEmail().isEmpty() ||
                member.getName() == null || member.getName().isEmpty() ||
                member.getPassword() == null || member.getPassword().isEmpty()) {
            throw new RegisterException.MissingFieldException("One or more fields are missing or null");
        }

        Optional<Member> existingMemberByLoginEmail = memberRepository.findByLoginEmail(member.getLoginEmail());
        Optional<Member> existingMemberByName = memberRepository.findByName(member.getName());

        if (existingMemberByName.isPresent()) {
            throw new RegisterException.NicknameAlreadyExistsException("Name already in use");
        }
        if (existingMemberByLoginEmail.isPresent()) {
            throw new RegisterException.EmailAlreadyExistsException("Login email already in use");
        }

        return memberRepository.save(member);
    }

    @Transactional
    public Optional<Member> authenticate(String email, String password) {
        return memberRepository.findByLoginEmailAndPassword(email, password);
    }

    @Transactional
    public Optional<Member> getMemberByLoginEmail(String loginEmail) {
        return memberRepository.findByLoginEmail(loginEmail);
    }

    @Transactional
    public Optional<Member> updateMember(ProfileRequestDto profileRequestDto, String loginEmail) throws Exception{
        Optional<Member> member = memberRepository.findByLoginEmail(loginEmail);

        if (member.isPresent()) {
            Member memberEntity = member.get();
            // Check for duplicate name
            Optional<Member> existingMemberByName = memberRepository.findByName(profileRequestDto.getName());
            if (existingMemberByName.isPresent() && !existingMemberByName.get().getLoginEmail().equals(loginEmail)) {
                throw new MyProfileException.NicknameAlreadyExistsException("Name already in use");
            }

            memberEntity.setName(profileRequestDto.getName());
            memberEntity.setProfileImageUrl(profileRequestDto.getProfileImageUrl());
            memberEntity.setDescription(profileRequestDto.getDescription());
            memberRepository.save(memberEntity);
        }else{
            throw new MyProfileException.ProfileNotFoundException("Member not found with email " + loginEmail);
        }
        return member;
    }

    @Transactional
    public void changePassword(PasswordRequestDto passwordRequestDto, String loginEmail) throws MyProfileException {
        Optional<Member> member = memberRepository.findByLoginEmail(loginEmail);

        if (member.isPresent()) {
            Member memberEntity = member.get();
            memberEntity.setPassword(passwordRequestDto.getPassword());
            memberRepository.save(memberEntity);
        } else {
            throw new MyProfileException.ProfileNotFoundException("Member not found with email " + loginEmail);
        }
    }

    @Transactional
    public void deleteMember(LoginRequestDto loginRequestDto, String authName) throws MyProfileException {
        // 현재 인증 이메일 체크
        if (!authName.equals(loginRequestDto.getLoginEmail())) {
            throw new MyProfileException.UnauthorizedAccessException("Invalid token");
        }
        // 아이디, 비밀번호 체크
        Optional<Member> member = memberRepository.findByLoginEmailAndPassword(loginRequestDto.getLoginEmail(), loginRequestDto.getPassword());
        if (member.isPresent()) {
            memberRepository.delete(member.get());
        } else {
            throw new MyProfileException.ProfileNotFoundException("Invalid loginEmail or password. : " + authName);
        }
    }

    @Transactional
    public void save(Member member) {
        memberRepository.save(member);
    }
}
