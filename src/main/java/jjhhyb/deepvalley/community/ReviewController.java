package jjhhyb.deepvalley.community;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jjhhyb.deepvalley.community.dto.request.ReviewPostRequest;
import jjhhyb.deepvalley.community.dto.response.PlaceImageResponse;
import jjhhyb.deepvalley.community.dto.response.ReviewDetailResponse;
import jjhhyb.deepvalley.community.dto.response.ReviewsResponse;
import jjhhyb.deepvalley.community.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Review", description = "리뷰 관리 API")
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping(value = "/api/review", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "리뷰 작성", description = "리뷰를 작성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 작성 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReviewDetailResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    public ReviewDetailResponse createReview(
            @RequestPart("reviewPostRequest") ReviewPostRequest reviewPostRequest,
            @RequestPart(value = "imageUrls", required = false) List<MultipartFile> imageFiles,
            Authentication auth
    ) {
        String userId = auth.getName(); // 인증이 되어 있는 UserID
        return reviewService.createReview(reviewPostRequest, imageFiles, userId);
    }

    @PutMapping(value = "/api/review/{review-id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "리뷰 수정", description = "리뷰를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 수정 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReviewDetailResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content),
            @ApiResponse(responseCode = "404", description = "리뷰를 찾을 수 없음", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    public ReviewDetailResponse updateReview(
            @PathVariable("review-id") String reviewId,
            @RequestPart("reviewPostRequest") ReviewPostRequest reviewPostRequest,
            @RequestParam(value = "imageUrls", required = false) List<MultipartFile> imageFiles,
            @RequestParam(value = "deletedImages", required = false) String deletedImages,
            Authentication auth
    ) {
        String userId = auth.getName(); // 인증이 되어 있는 UserID
        return reviewService.updateReview(reviewId, reviewPostRequest, imageFiles, deletedImages, userId);
    }

    @DeleteMapping("/api/review/{review-id}")
    @Operation(summary = "리뷰 삭제", description = "리뷰를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 삭제 성공", content = @Content),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content),
            @ApiResponse(responseCode = "404", description = "리뷰를 찾을 수 없음", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    public ResponseEntity<String> deleteReview(
            @PathVariable("review-id") String reviewId,
            Authentication auth
    ) {
        String userId = auth.getName(); // 인증이 되어 있는 UserID
        reviewService.deleteReview(reviewId, userId);
        return ResponseEntity.ok(reviewId); // 삭제된 리뷰 ID를 반환
    }

    @GetMapping("/api/valley/{valley-id}/image")
    @Operation(summary = "리뷰 이미지 조회", description = "특정 계곡에 대한 리뷰 이미지를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 이미지 검색 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PlaceImageResponse.class))),
            @ApiResponse(responseCode = "404", description = "계곡을 찾을 수 없음", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    public List<PlaceImageResponse> searchReviewImage(
            @PathVariable("valley-id") String placeId,
            Authentication auth
    ) {
        String userId = auth.getName();
        return reviewService.searchReviewImage(placeId, userId);
    }

    @GetMapping("/api/valley/{valley-id}/review")
    @Operation(summary = "특정 계곡 리뷰 조회", description = "특정 계곡의 리뷰를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReviewsResponse.class))),
            @ApiResponse(responseCode = "404", description = "계곡을 찾을 수 없음", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    public ReviewsResponse getPlaceReviews(
            @PathVariable("valley-id") String placeId,
            Authentication auth
    ) {
        String userId = auth.getName();
        return reviewService.getPlaceReviews(placeId, userId);
    }

    @GetMapping("/api/review/{review-id}/detail")
    @Operation(summary = "리뷰 상세 조회", description = "리뷰의 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 상세 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReviewDetailResponse.class))),
            @ApiResponse(responseCode = "404", description = "리뷰를 찾을 수 없음", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    public ReviewDetailResponse getReviewDetail(
            @PathVariable("review-id") String reviewId,
            Authentication auth
    ) {
        String userId = auth.getName();
        return reviewService.getReviewDetail(reviewId, userId);
    }

    @GetMapping("/api/member/{member-id}/review")
    @Operation(summary = "특정 회원의 리뷰 조회", description = "특정 회원의 리뷰를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 리뷰 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReviewsResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    public ReviewsResponse getSpecificMemberReviews(
            @PathVariable("member-id") String loginEmail,
            Authentication auth
    ) {
        String userId = auth.getName();
        return reviewService.getSpecificMemberReviews(loginEmail, userId);
    }
}