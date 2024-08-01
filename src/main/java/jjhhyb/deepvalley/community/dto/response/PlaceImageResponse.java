package jjhhyb.deepvalley.community.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PlaceImageResponse {
    private String reviewId;
    private String title;
    private String content;
    private List<String> imageUrls;
    private String profile_image_url;
}
