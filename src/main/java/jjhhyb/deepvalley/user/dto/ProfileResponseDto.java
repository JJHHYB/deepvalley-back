package jjhhyb.deepvalley.user.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jjhhyb.deepvalley.user.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ProfileResponseDto {
    private String loginEmail;
    private String name;
    private String profileImageUrl;
    private String description;
    private LocalDateTime createdDate;
    private LocalDateTime loginDate;
    private String oauth;

    public static ProfileResponseDto fromMember(Member member) {
        return ProfileResponseDto.builder()
                .loginEmail(member.getLoginEmail())
                .name(member.getName())
                .profileImageUrl(member.getProfileImageUrl())
                .description(member.getDescription())
                .createdDate(member.getCreatedDate())
                .loginDate(member.getLoginDate())
                .oauth(member.getOauth())
                .build();
    }
}