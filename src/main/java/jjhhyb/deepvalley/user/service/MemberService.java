package jjhhyb.deepvalley.user.service;

import jjhhyb.deepvalley.community.repository.ReviewRepository;
import jjhhyb.deepvalley.image.ImageService;
import jjhhyb.deepvalley.image.ImageType;
import jjhhyb.deepvalley.user.dto.LoginRequestDto;
import jjhhyb.deepvalley.user.dto.PasswordRequestDto;
import jjhhyb.deepvalley.user.dto.ProfileRequestDto;
import jjhhyb.deepvalley.user.entity.Member;
import jjhhyb.deepvalley.user.exception.MyProfileException;
import jjhhyb.deepvalley.user.exception.RegisterException;
import jjhhyb.deepvalley.user.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final ReviewRepository reviewRepository;
    private final ImageService imageService;

    public MemberService(MemberRepository memberRepository, ReviewRepository reviewRepository, ImageService imageService) {
        this.memberRepository = memberRepository;
        this.reviewRepository = reviewRepository;
        this.imageService = imageService;
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
    public Optional<Member> updateMember(ProfileRequestDto profileRequestDto, MultipartFile profileImage, String loginEmail) throws Exception{
        Optional<Member> member = memberRepository.findByLoginEmail(loginEmail);

        if (member.isPresent()) {
            String profileUrl = "";
            Member memberEntity = member.get();
            // Check for duplicate name
            Optional<Member> existingMemberByName = memberRepository.findByName(profileRequestDto.getName());
            if (existingMemberByName.isPresent() && !existingMemberByName.get().getLoginEmail().equals(loginEmail)) {
                throw new MyProfileException.NicknameAlreadyExistsException("Name already in use");
            }

            // 현재 등록된 url이 있을 때
            if (memberEntity.getProfileImageUrl() != null && !memberEntity.getProfileImageUrl().isEmpty()) {
                // 프로필이미지 수정이 되었을 때 기존 파일 S3에서 삭제하고 신규 파일 업로드
                if (profileImage != null) {
                    imageService.deleteImages(List.of(memberEntity.getProfileImageUrl()));
                    List<String> imageUrls = imageService.uploadImagesAndGetUrls(List.of(profileImage), ImageType.PROFILE);
                    profileUrl = imageUrls.get(0);
                } else{ // 프로필이미지 수정이 되지 않았을 때
                    profileUrl = memberEntity.getProfileImageUrl();
                }
            } else{ // 현재 등록된 url이 없을 때
                if (profileImage != null) { // 신규 파일 업로드
                    List<String> imageUrls = imageService.uploadImagesAndGetUrls(List.of(profileImage), ImageType.PROFILE);
                    profileUrl = imageUrls.get(0);
                }
            }

            memberEntity.setName(profileRequestDto.getName());
            memberEntity.setProfileImageUrl(profileUrl);
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
            if (!memberEntity.getPassword().equals(passwordRequestDto.getOldPassword())) {
                throw new MyProfileException.InvalidPasswordException("Invalid Old Password");
            }
            if (memberEntity.getPassword().equals(passwordRequestDto.getNewPassword())){
                throw new MyProfileException.SamePasswordException("New password is the same as Old password");
            }
            memberEntity.setPassword(passwordRequestDto.getNewPassword());
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
        Optional<Member> optionalMember = memberRepository.findByLoginEmailAndPassword(loginRequestDto.getLoginEmail(), loginRequestDto.getPassword());
        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();

            // review 테이블에서 해당 member_id를 참조하는 행 삭제
            reviewRepository.deleteByMember(member);

            // member 테이블에서 회원 삭제
            memberRepository.deleteById(member.getMemberId());
        } else {
            throw new MyProfileException.ProfileNotFoundException("Invalid loginEmail or password. : " + authName);
        }
    }

    @Transactional
    public void save(Member member) {
        memberRepository.save(member);
    }
}
