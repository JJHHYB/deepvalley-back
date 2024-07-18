package jjhhyb.deepvalley.community;

import jjhhyb.deepvalley.community.dto.request.ReviewPostRequest;
import jjhhyb.deepvalley.community.dto.response.PlaceImageResponse;
import jjhhyb.deepvalley.community.dto.response.ReviewDetailResponse;
import jjhhyb.deepvalley.community.dto.response.ReviewsResponse;
import jjhhyb.deepvalley.community.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping("/api/review")
    public ReviewDetailResponse createReview(
            @RequestBody ReviewPostRequest reviewPostRequest,
            Authentication auth
    ) {
        String userId = auth.getName(); // 인증이 되어 있는 UserID
        return reviewService.createReview(reviewPostRequest, userId);
    }

    @PutMapping("/api/review/{review-id}")
    public ReviewDetailResponse updateReview(
            @PathVariable("review-id") Long reviewId,
            @RequestBody ReviewPostRequest reviewPostRequest,
            Authentication auth
    ) {
        String userId = auth.getName(); // 인증이 되어 있는 UserID
        return reviewService.updateReview(reviewId, reviewPostRequest, userId);
    }

    @DeleteMapping("/api/review/{review-id}")
    public ResponseEntity<Long> deleteReview(
            @PathVariable("review-id") Long reviewId,
            Authentication auth
    ) {
        String userId = auth.getName(); // 인증이 되어 있는 UserID
        reviewService.deleteReview(reviewId, userId);
        return ResponseEntity.ok(reviewId); // 삭제된 리뷰 ID를 반환
    }

    @GetMapping("/api/valley/{valley-id}/image")
    public List<PlaceImageResponse> searchReviewImage(
            @PathVariable("valley-id") Long placeId
    ){
        return reviewService.searchReviewImage(placeId);
    }

    @GetMapping("/api/valley/{valley-id}/review")
    public ReviewsResponse getPlaceReviews(
            @PathVariable("valley-id") Long placeId
    ) {
        return reviewService.getPlaceReviews(placeId);
    }

    @GetMapping("/api/review/{review-id}/detail")
    public ReviewDetailResponse getReviewDetail(
            @PathVariable("review-id") Long reviewId
    ) {
        return reviewService.getReviewDetail(reviewId);
    }
}
