package jjhhyb.deepvalley.user.dto;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RegisterResponseDto {
    private Long memberId;
    private String loginEmail;
    private String name;
    private String password;
    private LocalDateTime createdDate;
}

