package jjhhyb.deepvalley.community.dto.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
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
