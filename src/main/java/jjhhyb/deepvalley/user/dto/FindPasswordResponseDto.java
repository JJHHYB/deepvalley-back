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
public class FindPasswordResponseDto {
    private String password;

    public static Object fromMember(Member m) {
        return FindPasswordResponseDto.builder()
                .password(m.getPassword())
                .build();
    }
}
