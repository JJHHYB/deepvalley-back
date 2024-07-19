package jjhhyb.deepvalley.user.service;

import jjhhyb.deepvalley.user.dto.LoginResponseDto;
import jjhhyb.deepvalley.user.entity.Member;
import jjhhyb.deepvalley.user.jwt.JWTUtil;
import jjhhyb.deepvalley.user.oauth.provider.kakao.KakaoTokenDto;
import jjhhyb.deepvalley.user.oauth.provider.kakao.KakaoUserInfoDto;
import jjhhyb.deepvalley.user.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class KakaoLoginService {

    @Value("${oauth.kakao.client-id}") private String clientId;
    @Value("${oauth.kakao.redirect-uri}") private String redirectUri;
    @Value("${oauth.kakao.url.auth}") private String authUrl;
    @Value("${oauth.kakao.url.api}") private String apiUrl;

    private final RestTemplate restTemplate;
    private final MemberRepository memberRepository;

    public KakaoLoginService(RestTemplate restTemplate, MemberRepository memberRepository) {
        this.restTemplate = restTemplate;
        this.memberRepository = memberRepository;
    }

    public KakaoTokenDto getAccessToken(String authorizationCode) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Accept", "application/json");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code"); //고정값
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri); //등록한 redirect uri
        params.add("code", authorizationCode);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<KakaoTokenDto> responseEntity = restTemplate.postForEntity(authUrl + "/oauth/token", request, KakaoTokenDto.class);
        log.info("response: {}", responseEntity);

        if (responseEntity != null) {
            return responseEntity.getBody();
        } else {
            throw new Exception("getAccessToken(Kakao) failed");
        }
    }

    public KakaoUserInfoDto getUserInfo(String accessToken) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBearerAuth(accessToken);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);

        ResponseEntity<KakaoUserInfoDto> responseEntity = restTemplate.postForEntity(apiUrl + "/v2/user/me", request, KakaoUserInfoDto.class);
        log.info("response: {}", responseEntity);

        if (responseEntity != null) {
            return responseEntity.getBody();
        } else {
            throw new Exception("getUserInfo(Kakao) failed");
        }
    }

    public LoginResponseDto handleLogin(String authorizationCode) throws Exception {
        KakaoTokenDto kakaoToken = getAccessToken(authorizationCode);
        KakaoUserInfoDto kakaoUserInfo = getUserInfo(kakaoToken.getAccessToken());
        KakaoUserInfoDto.KakaoAccount.Profile kakaoProfile = kakaoUserInfo.getKakaoAccount().getProfile();

        // 카카오에서 받아온 사용자 정보
        Long kakaoId                     = kakaoUserInfo.getId();
        String loginEmail = kakaoId.toString() + "@kakao.com"; // 카카오는 이메일을 기본 제공하지 않음, 유저 구분을 위해 kakaoId로 대체
        String kakaoNickname             = kakaoProfile.getNickname();
        String kakaoProfileImageUrl      = kakaoProfile.getProfileImageUrl();
        Instant profileConnectedDate     = Instant.parse(kakaoUserInfo.getConnectedAt());
        LocalDateTime kakaoConnectedDate = LocalDateTime.ofInstant(profileConnectedDate, ZoneId.systemDefault());

        Optional<Member> member = memberRepository.findByLoginEmail(loginEmail);
        //Optional<Member> member = memberRepository.findByName(nickname);
        if (member.isPresent()) { // 회원이 있으면 업데이트
            Member memberEntity = member.get();
            //자체 서비스에서도 닉네임, 프로필 이미지 변경 가능성이 있으므로, 시간만 업데이트
            //memberEntity.setName((!Objects.equals(memberEntity.getName(), kakaoNickname)) ? kakaoNickname : memberEntity.getName());
            //memberEntity.setProfileImageUrl((!Objects.equals(memberEntity.getProfileImageUrl(), kakaoProfileImageUrl)) ? kakaoProfileImageUrl : memberEntity.getProfileImageUrl());
            memberEntity.setCreatedDate((!Objects.equals(memberEntity.getCreatedDate(), kakaoConnectedDate)) ? kakaoConnectedDate : memberEntity.getCreatedDate());
            memberEntity.setLoginDate(LocalDateTime.now());
            memberRepository.save(memberEntity);
        }
        else{ // 회원이 없으면 새로 등록
            Member newMember = new Member();
            newMember.setLoginEmail(loginEmail);
            newMember.setPassword(UUID.randomUUID().toString()); // 임시 비밀번호
            newMember.setName(kakaoNickname);
            newMember.setProfileImageUrl(kakaoProfileImageUrl);
            newMember.setCreatedDate(kakaoConnectedDate);
            newMember.setLoginDate(LocalDateTime.now());
            memberRepository.save(newMember);
        }

        String token = JWTUtil.createJwt(loginEmail, "USER", 60*60*1000L);
        return new LoginResponseDto(token);
    }
}
