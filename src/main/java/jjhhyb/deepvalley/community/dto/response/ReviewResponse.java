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
        return ReviewResponse.builder()
                .reviewId(review.getReviewId())
                .title(review.getTitle())
                .rating(review.getRating().name())
                .content(review.getContent())
                .visitedDate(review.getVisitedDate())
                .privacy(review.getPrivacy().name())
                .memberId(review.getMemberId())
                .placeId(review.getPlaceId())
                .createdDate(review.getCreatedDate())
                .updatedDate(review.getUpdatedDate())
                .imageUrls(review.getReviewImages().stream()
                        .map(reviewImage -> reviewImage.getImage().getImageUrl())
                        .collect(Collectors.toList()))
                .tagNames(review.getReviewTags().stream()
                        .map(reviewTag -> reviewTag.getTag().getName())
                        .collect(Collectors.toList()))
                .build();
    }
}
