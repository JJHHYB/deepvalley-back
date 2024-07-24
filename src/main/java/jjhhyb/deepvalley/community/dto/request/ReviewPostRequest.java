package jjhhyb.deepvalley.community.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Data
@Builder
public class ReviewPostRequest {
    private String title;
    private String rating;
    private String content;
    private String visitedDate;
    private String privacy;
    private String placeId;
    private List<String> tagNames;
    private List<String> imageUrls;
}
