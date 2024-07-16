package jjhhyb.deepvalley.community;

import jjhhyb.deepvalley.community.dto.request.ReviewPostRequest;
import jjhhyb.deepvalley.community.dto.response.ReviewResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping("/api/review")
    public ReviewResponse createReview(
            @RequestBody ReviewPostRequest reviewPostRequest
    ) {
        return reviewService.createReview(reviewPostRequest);
    }

    @PutMapping("/api/review/{review-id}")
    public ReviewResponse updateReview(
            @PathVariable("review-id") Long reviewId,
            @RequestBody ReviewPostRequest reviewPostRequest
    ) {
        return reviewService.updateReview(reviewId, reviewPostRequest);
    }

    @DeleteMapping("/api/review/{review-id}")
    public ResponseEntity<Long> deleteReview(
            @PathVariable("review-id") Long reviewId
    ) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.ok(reviewId); // 삭제된 리뷰 ID를 반환
    }
}
