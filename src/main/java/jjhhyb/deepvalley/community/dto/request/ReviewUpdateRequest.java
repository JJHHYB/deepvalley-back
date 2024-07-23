package jjhhyb.deepvalley.community.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class ReviewUpdateRequest {
    private String title;
    private String rating;
    private String content;
    private String visitedDate;
    private String privacy;
    private List<String> tagNames;
    private List<String> imageUrls;
}
