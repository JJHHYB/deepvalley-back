package jjhhyb.deepvalley.community.dto.response;

import jjhhyb.deepvalley.community.entity.Review;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewDetailResponse {
    private String reviewId;
    private String uuid;
    private String title;
    private String rating;
    private String content;
    private String visitedDate;
    private String privacy;
    private String memberId;
    private String placeId;
    private String valleyName;
    private String createdDate;
    private String updatedDate;
    private List<String> imageUrls;
    private List<String> tagNames;

    public static ReviewDetailResponse from(Review review) {
        return ReviewDetailResponse.builder()
                .reviewId(String.valueOf(review.getReviewId()))
                .uuid(String.valueOf(review.getUuid()))
                .title(review.getTitle())
                .rating(review.getRating().name())
                .content(review.getContent())
                .visitedDate(String.valueOf(review.getVisitedDate()))
                .privacy(review.getPrivacy().name())
                .memberId(String.valueOf(review.getMember().getMemberId()))
                .placeId(String.valueOf(review.getPlace().getPlaceId()))
                .valleyName(review.getPlace().getName())
                .createdDate(String.valueOf(review.getCreatedDate()))
                .updatedDate(String.valueOf(review.getUpdatedDate()))
                .imageUrls(review.getReviewImages().stream()
                        .map(reviewImage -> reviewImage.getImage().getImageUrl())
                        .collect(Collectors.toList()))
                .tagNames(review.getReviewTags().stream()
                        .map(reviewTag -> reviewTag.getTag().getName())
                        .collect(Collectors.toList()))
                .build();
    }
}