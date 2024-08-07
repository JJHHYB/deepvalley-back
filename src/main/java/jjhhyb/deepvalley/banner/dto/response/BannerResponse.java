package jjhhyb.deepvalley.banner.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class BannerResponse {
    private List<String> imageUrls;

    public static BannerResponse from(List<String> imageUrls) {
        return BannerResponse.builder()
                .imageUrls(imageUrls)
                .build();
    }
}
