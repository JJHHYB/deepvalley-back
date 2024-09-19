package jjhhyb.deepvalley.community.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jjhhyb.deepvalley.community.entity.Review;
import lombok.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ReviewDetailResponse {
    private String reviewId;
    private String title;
    private String rating;
    private String content;
    private String visitedDate;
    private String privacy;
    private String memberId;
    private String memberName;
    private String placeId;
    private String valleyName;
    private String createdDate;
    private String updatedDate;
    private List<String> imageUrls;
    private List<String> tagNames;
    private String profileImageUrl;
    public static ReviewDetailResponse from(Review review) {
        return ReviewDetailResponse.builder()
                .reviewId(String.valueOf(review.getUuid()))
                .title(review.getTitle())
                .rating(review.getRating().name())
                .content(review.getContent())
                .visitedDate(String.valueOf(review.getVisitedDate()))
                .privacy(review.getPrivacy().name())
                .memberId(String.valueOf(review.getMember().getLoginEmail()))
                .memberName(String.valueOf(review.getMember().getName()))
                .placeId(String.valueOf(review.getPlace().getUuid()))
                .valleyName(review.getPlace().getName())
                .createdDate(String.valueOf(review.getCreatedDate()))
                .updatedDate(String.valueOf(review.getUpdatedDate()))
                .imageUrls((review.getReviewImages() != null ? review.getReviewImages().stream()
                        .map(reviewImage -> reviewImage.getImage().getImageUrl())
                        .collect(Collectors.toList()) : Collections.emptyList()))
                .tagNames(review.getReviewTags().stream()
                        .map(reviewTag -> reviewTag.getTag().getName())
                        .collect(Collectors.toList()))
                .profileImageUrl(review.getMember().getProfileImageUrl())
                .build();
    }
}