package jjhhyb.deepvalley.community.dto.response;

import jjhhyb.deepvalley.community.entity.Image;
import jjhhyb.deepvalley.community.entity.Review;
import jjhhyb.deepvalley.tag.entity.Tag;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewResponse {
    private Long reviewId;
    private String title;
    private String rating;
    private String content;
    private LocalDate visitedDate;
    private String privacy;
    private Long memberId;
    private Long placeId;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private List<String> imageUrls;
    private List<String> tagNames;

    // Review 객체를 ReviewResponse 객체로 변환
    public static ReviewResponse from(Review review) {
        ReviewResponse response = new ReviewResponse();
        response.setReviewId(review.getReviewId());
        response.setTitle(review.getTitle());
        response.setRating(review.getRating().name());
        response.setContent(review.getContent());
        response.setVisitedDate(review.getVisitedDate() != null ? review.getVisitedDate() : null);
        response.setPrivacy(review.getPrivacy().name());
        response.setMemberId(review.getMemberId());
        response.setPlaceId(review.getPlaceId());
        response.setCreatedDate(review.getCreatedDate());
        response.setUpdatedDate(review.getUpdatedDate());

        // 이미지 URL 리스트 생성
        response.setImageUrls(review.getReviewImages().stream()
                .map(reviewImage -> reviewImage.getImage().getImageUrl())
                .collect(Collectors.toList()));

        // 태그 이름 리스트 생성
        response.setTagNames(review.getReviewTags().stream()
                .map(reviewTag -> reviewTag.getTag().getName())
                .collect(Collectors.toList()));
        return response;
    }
}
