package jjhhyb.deepvalley.community.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class ReviewDetailResponse {
    private String reviewId;
    private String title;
    private String rating;
    private String content;
    private String visitedDate;
    private String privacy;
    private String createdDate;
    private String updatedDate;
    private List<String> tagNames;
    private List<String> imageUrls;
    private String valleyName;
    private String valleyId;
}