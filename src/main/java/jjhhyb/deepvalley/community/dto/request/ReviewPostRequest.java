package jjhhyb.deepvalley.community.dto.request;

import lombok.Builder;
import lombok.Data;
import lombok.Setter;

import java.util.List;

@Data
@Builder
public class ReviewPostRequest {
    private String title;
    private String rating;
    private String content;
    private String visitedDate;
    private String privacy;
    private String valleyId;
    private List<String> tagNames;
    private List<String> imageUrls;
}
