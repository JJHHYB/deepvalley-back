package jjhhyb.deepvalley.community.service;

import jakarta.persistence.EntityNotFoundException;
import jjhhyb.deepvalley.community.repository.ReviewImageRepository;
import jjhhyb.deepvalley.community.repository.ReviewRepository;
import jjhhyb.deepvalley.community.dto.response.PlaceImageResponse;
import jjhhyb.deepvalley.community.dto.response.ReviewDetailResponse;
import jjhhyb.deepvalley.community.dto.response.ReviewsResponse;
import jjhhyb.deepvalley.community.ReviewNotFoundException;
import jjhhyb.deepvalley.place.Place;
import jjhhyb.deepvalley.place.PlaceRepository;
import jjhhyb.deepvalley.tag.ReviewTagRepository;
import jjhhyb.deepvalley.community.dto.request.ReviewPostRequest;
import jjhhyb.deepvalley.community.entity.*;
import jjhhyb.deepvalley.tag.entity.ReviewTag;
import jjhhyb.deepvalley.user.entity.Member;
import jjhhyb.deepvalley.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewImageService reviewImageService;
    private final ReviewTagService reviewTagService;
    private final ReviewImageRepository reviewImageRepository;
    private final ReviewTagRepository reviewTagRepository;
    private final MemberRepository memberRepository;
    private final PlaceRepository placeRepository;

    private static final String USER_NOT_FOUND = "사용자를 찾을 수 없습니다.";
    private static final String REVIEW_NOT_FOUND = "리뷰를 찾을 수 없습니다.";
    private static final String INVALID_PLACE_ID = "유효하지 않은 장소 ID입니다.";
    private static final String INVALID_DATE_FORMAT = "유효하지 않은 날짜 형식 : ";
    private static final String NOT_USER_REVIEW = "사용자가 작성한 리뷰가 아닙니다.";
    @Transactional
    public ReviewDetailResponse createReview(ReviewPostRequest request, String userId) {
        // userId를 이용하여 Member 엔티티 조회
        Member member = findMemberByUserId(userId);

        // Place 엔티티 생성 (테스트 데이터를 사용)
        Place place = createPlaceWithRandomData();

        // Review 엔티티 생성
        Review review = createReviewEntity(request, member, place);

        // 생성한 Review 엔티티 데이터베이스에 저장
        Review savedReview = reviewRepository.save(review);

        // 이미지, 태그 처리
        List<ReviewImage> reviewImages = reviewImageService.processImages(request.getImageUrls(), savedReview);
        List<ReviewTag> reviewTags = reviewTagService.processTags(request.getTagNames(), savedReview);

        // 생성된 리뷰에 이미지와 태그를 추가하고, 업데이트된 리뷰를 데이터베이스에 저장
        updateReviewWithImagesAndTags(savedReview, reviewImages, reviewTags);

        // 응답 객체로 변환 후 반환
        return ReviewDetailResponse.from(savedReview);
    }

    // 리뷰 업데이트
    @Transactional
    public ReviewDetailResponse updateReview(Long reviewId, ReviewPostRequest request, String userId) {
        // 리뷰 존재 여부 및 작성자 확인
        Review updateReview = validateReviewOwner(reviewId, userId);

        // 리뷰 엔티티 업데이트
        updateReviewEntity(updateReview, request);

        // 이미지, 태그 처리
        List<ReviewImage> updatedReviewImages = reviewImageService.processImages(request.getImageUrls(), updateReview);
        List<ReviewTag> updatedReviewTags = reviewTagService.processTags(request.getTagNames(), updateReview);

        // 기존의 이미지, 태그 제거 및 업데이트
        reviewImageService.updateReviewImages(updateReview, updatedReviewImages);
        reviewTagService.updateReviewTags(updateReview, updatedReviewTags);

        // 업데이트된 리뷰 저장
        reviewRepository.save(updateReview);

        // 응답 객체로 변환 후 반환
        return ReviewDetailResponse.from(updateReview);
    }

    @Transactional
    public void deleteReview(Long reviewId, String userId) {
        // 리뷰 존재 여부 및 작성자 확인
        Review review = validateReviewOwner(reviewId, userId);

        // 리뷰와 연관된 모든 이미지 삭제
        List<ReviewImage> reviewImages = reviewImageService.findByReviewId(review.getReviewId());
        reviewImageService.deleteAll(reviewImages);

        // 리뷰와 연관된 모든 태그 삭제
        List<ReviewTag> reviewTags = reviewTagService.findByReviewId(review.getReviewId());
        reviewTagService.deleteAll(reviewTags);

        // 리뷰 삭제
        reviewRepository.delete(review);
    }

    @Transactional(readOnly = true)
    public List<PlaceImageResponse> searchReviewImage(Long placeId) {
        // 리뷰를 통해 해당 장소에 대한 모든 리뷰를 조회합니다.
        List<Review> reviews = reviewRepository.findByPlace_PlaceId(placeId);

        // 각 리뷰에 대한 이미지 URL을 가져와서 PlaceImageResponse 객체를 생성
        return reviews.stream()
                .map(review -> {
                    List<String> imageUrls = reviewImageRepository.findByReview_ReviewId(review.getReviewId()).stream()
                            .map(reviewImage -> reviewImage.getImage().getImageUrl())
                            .collect(Collectors.toList());
                    return new PlaceImageResponse(review.getReviewId(), imageUrls);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReviewsResponse getPlaceReviews(Long placeId) {
        // 데이터베이스에서 해당 계곡(장소)의 리뷰 목록을 조회
        List<Review> reviews = reviewRepository.findByPlace_PlaceId(placeId);

        // Review 엔터티를 ReviewResponse로 변환
        List<ReviewDetailResponse> reviewDetailResponses = reviews.stream()
                .map(ReviewDetailResponse::from)
                .collect(Collectors.toList());

        // ReviewsResponse 객체에 변환된 리뷰 목록을 설정
        ReviewsResponse reviewsResponse = new ReviewsResponse();
        reviewsResponse.setReviews(reviewDetailResponses);

        return reviewsResponse;
    }

    @Transactional(readOnly = true)
    public ReviewDetailResponse getReviewDetail(Long reviewId) {
        // 데이터베이스에서 리뷰를 ID로 조회
        Optional<Review> optionalReview = reviewRepository.findById(reviewId);

        // 리뷰가 존재하지 않으면 예외 처리
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(REVIEW_NOT_FOUND));

        // Review 엔터티를 ReviewDetailResponse로 변환
        return ReviewDetailResponse.from(review);
    }

    // userId를 이용하여 Member 엔티티 조회
    private Member findMemberByUserId(String userId) {
        return memberRepository.findByLoginEmail(userId)
                .orElseThrow(() -> new IllegalArgumentException(USER_NOT_FOUND));
    }

    // 리뷰 존재 여부 및 작성자 확인
    private Review validateReviewOwner(Long reviewId, String userId) {
        // 리뷰 존재 여부 확인
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException(REVIEW_NOT_FOUND + " with id: " + reviewId));
        // userId를 이용하여 Member 엔티티 조회
        Member member = findMemberByUserId(userId);
        // 작성자 검증
        if (!review.getMemberId().equals(member.getMemberId())) {
            throw new IllegalArgumentException(NOT_USER_REVIEW);
        }
        return review;
    }

    // 리뷰 엔티티 생성
    private Review createReviewEntity(ReviewPostRequest request, Member member, Place place) {
        LocalDate visitedDate = parseVisitedDate(request.getVisitedDate());

        // Place 엔티티를 ID로 조회
        place = placeRepository.findById(Long.valueOf(request.getPlaceId()))
                .orElseThrow(() -> new IllegalArgumentException(INVALID_PLACE_ID));

        return Review.builder()
                .uuid(UUID.randomUUID().toString())
                .title(request.getTitle())
                .rating(ReviewRating.valueOf(request.getRating()))
                .content(request.getContent())
                .visitedDate(visitedDate)
                .privacy(ReviewPrivacy.valueOf(request.getPrivacy()))
                .memberId(member.getMemberId())
                .place(place)
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();
    }

    // 방문 날짜 문자열을 LocalDate로 변환
    private LocalDate parseVisitedDate(String visitedDateStr) {
        if (visitedDateStr == null || visitedDateStr.isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(visitedDateStr);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(INVALID_DATE_FORMAT + visitedDateStr, e);
        }
    }

    // 리뷰에 이미지와 태그 추가 및 업데이트
    private void updateReviewWithImagesAndTags(Review review, List<ReviewImage> reviewImages, List<ReviewTag> reviewTags) {
        review.setReviewImages(reviewImages);
        review.setReviewTags(reviewTags);
        reviewRepository.save(review);
        reviewImageRepository.saveAll(reviewImages);
        reviewTagRepository.saveAll(reviewTags);
    }

    private void updateReviewEntity(Review review, ReviewPostRequest request) {
        review.setTitle(request.getTitle());
        review.setRating(ReviewRating.valueOf(request.getRating()));
        review.setContent(request.getContent());
        review.setVisitedDate(parseVisitedDate(request.getVisitedDate()));
        review.setPrivacy(ReviewPrivacy.valueOf(request.getPrivacy()));
        review.setUpdatedDate(LocalDateTime.now());
    }

    /*
        Place에 테스트 데이터를 임의로 생성
    */
    @Transactional
    public Place createPlaceWithRandomData() {
        Place place = new Place();
        place.setName("Test Place");
        place.setUuid(UUID.randomUUID().toString());
        place.setAddress("123 Test St, Test City, Test Country");
        place.setRegion("Test Region");
        place.setContent("This is a test place.");
        place.setLatitude(BigDecimal.valueOf(37.7749)); // Example latitude
        place.setLongitude(BigDecimal.valueOf(-122.4194)); // Example longitude
        place.setCreatedDate(LocalDateTime.now());
        place.setUpdatedDate(LocalDateTime.now());

        return placeRepository.save(place);
    }

}
