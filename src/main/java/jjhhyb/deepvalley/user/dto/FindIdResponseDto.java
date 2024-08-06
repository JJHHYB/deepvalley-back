package jjhhyb.deepvalley.user.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jjhhyb.deepvalley.user.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class FindIdResponseDto{
    private String loginEmail;

    public static Object fromMember(Member m) {
        return FindIdResponseDto.builder()
                .loginEmail(m.getLoginEmail())
                .build();
    }
}
