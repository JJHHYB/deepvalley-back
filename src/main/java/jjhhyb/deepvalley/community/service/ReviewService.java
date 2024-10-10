package jjhhyb.deepvalley.community.service;

import jakarta.persistence.EntityNotFoundException;
import jjhhyb.deepvalley.community.repository.ReviewImageRepository;
import jjhhyb.deepvalley.community.repository.ReviewRepository;
import jjhhyb.deepvalley.community.dto.response.PlaceImageResponse;
import jjhhyb.deepvalley.community.dto.response.ReviewDetailResponse;
import jjhhyb.deepvalley.community.dto.response.ReviewsResponse;
import jjhhyb.deepvalley.community.ReviewNotFoundException;
import jjhhyb.deepvalley.image.ImageService;
import jjhhyb.deepvalley.image.ImageType;
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
import org.springframework.web.multipart.MultipartFile;

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
    private final ImageService imageService;
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
    public ReviewDetailResponse createReview(ReviewPostRequest request, List<MultipartFile> imageFiles, String userId) {
        // userId를 이용하여 Member 엔티티 조회
        Member member = findMemberByUserId(userId);

        // Review 엔티티 생성
        Review review = createReviewEntity(request, member);

        // 생성한 Review 엔티티 데이터베이스에 저장
        Review savedReview = reviewRepository.save(review);

        handleImageUpload(imageFiles, savedReview);

        // 태그 처리
        List<ReviewTag> reviewTags = reviewTagService.processTags(request.getTagNames(), savedReview);
        updateReviewWithTags(savedReview, reviewTags);

        updatePlaceMetrics(savedReview.getPlace().getUuid());
        // 응답 객체로 변환 후 반환
        return ReviewDetailResponse.from(savedReview);
    }

    // 리뷰 업데이트
    @Transactional
    public ReviewDetailResponse updateReview(String reviewId, ReviewPostRequest request, List<MultipartFile> newImages, List<String> deletedImages, String userId) {
        // 리뷰 존재 여부 및 작성자 확인
        Review updateReview = validateReviewOwner(reviewId, userId);

        // 리뷰 엔티티 업데이트
        updateReviewEntity(updateReview, request);

        // 삭제할 이미지가 있는 경우
        if (deletedImages != null && !deletedImages.isEmpty()) {
            // 삭제할 이미지 URL에 해당하는 ReviewImage 엔티티 조회
            List<ReviewImage> reviewImagesToDelete = reviewImageRepository.findByImage_ImageUrlIn(deletedImages);

            // 삭제할 이미지를 컬렉션에서 제거
            updateReview.getReviewImages().removeAll(reviewImagesToDelete);

            // 이미지 삭제 처리 (S3 및 DB에서 삭제)
            reviewImageService.deleteAll(reviewImagesToDelete);
        }

        // 새로운 이미지를 업로드하는 경우
        handleImageUpload(newImages, updateReview);

        // 태그 처리
        List<ReviewTag> updatedReviewTags = reviewTagService.processTags(request.getTagNames(), updateReview);
        reviewTagService.updateReviewTags(updateReview, updatedReviewTags);

        // 업데이트된 리뷰 저장
        reviewRepository.save(updateReview);

        // 리뷰와 연결된 장소 정보 업데이트
        updatePlaceMetrics(updateReview.getPlace().getUuid());

        return ReviewDetailResponse.from(updateReview);
    }

    @Transactional
    public void deleteReview(String reviewId, String userId) {
        // 리뷰 존재 여부 및 작성자 확인
        Review review = validateReviewOwner(reviewId, userId);

        // 리뷰와 연관된 모든 이미지, 태그 삭제
        List<ReviewImage> reviewImages = reviewImageRepository.findByReview_ReviewId(review.getReviewId());
        reviewImageService.deleteAll(reviewImages);
        List<ReviewTag> reviewTags = reviewTagService.findByReviewId(review.getReviewId());
        reviewTagService.deleteAll(reviewTags);

        // 리뷰 삭제
        reviewRepository.delete(review);

        updatePlaceMetrics(review.getPlace().getUuid());
    }

    @Transactional(readOnly = true)
    public List<PlaceImageResponse> searchReviewImage(String placeId, String userId) {
        // 특정 장소에 대한 모든 리뷰 조회
        List<Review> reviews = reviewRepository.findAllByPlace_Uuid(placeId);

        // 리뷰에 대해 작성자 여부와 공개 여부를 확인하고 필터링
        return reviews.stream()
                .filter(review -> review.getMember().getLoginEmail().equals(userId) || review.getPrivacy() == ReviewPrivacy.PUBLIC)
                .map(review -> {
                    List<String> imageUrls = reviewImageRepository.findByReview_ReviewId(review.getReviewId()).stream()
                            .map(reviewImage -> reviewImage.getImage().getImageUrl())
                            .collect(Collectors.toList());

                    if (!imageUrls.isEmpty()) {
                        return new PlaceImageResponse(review.getUuid(), review.getMember().getName(), review.getTitle(), review.getContent(), review.getVisitedDate(), imageUrls, review.getMember().getProfileImageUrl());
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull) // null 필터링
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReviewsResponse getPlaceReviews(String placeId, String userId) {
        // 특정 장소에 대한 모든 리뷰 조회
        List<Review> reviews = reviewRepository.findAllByPlace_Uuid(placeId);

        // 각 리뷰에 대해 작성자 여부와 공개 여부를 확인하고 필터링
        List<ReviewDetailResponse> reviewDetailResponses = reviews.stream()
                .filter(review -> review.getMember().getLoginEmail().equals(userId) || review.getPrivacy() == ReviewPrivacy.PUBLIC)
                .map(ReviewDetailResponse::from)
                .collect(Collectors.toList());

        // 필터링된 리뷰 목록을 ReviewsResponse로 변환하여 반환
        ReviewsResponse reviewsResponse = new ReviewsResponse();
        reviewsResponse.setReviews(reviewDetailResponses);

        return reviewsResponse;
    }

    @Transactional(readOnly = true)
    public ReviewDetailResponse getReviewDetail(String reviewId, String userId) {
        // 리뷰를 조회하고, 존재하지 않으면 예외 처리
        Review review = reviewRepository.findByUuid(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(REVIEW_NOT_FOUND));

        // 로그인된 사용자가 리뷰 작성자인지 확인
        boolean isOwner = review.getMember().getLoginEmail().equals(userId);

        // 리뷰가 PUBLIC이 아니고, 작성자도 아니라면 예외 처리
        if (!isOwner && review.getPrivacy() != ReviewPrivacy.PUBLIC) {
            throw new ReviewNotFoundException(REVIEW_NOT_FOUND);
        }

        // Review 엔터티를 ReviewDetailResponse로 변환하여 반환
        return ReviewDetailResponse.from(review);
    }

    @Transactional(readOnly = true)
    public ReviewsResponse getSpecificMemberReviews(String loginEmail, String userId) {
        // loginEmail이 null인지 확인
        if (loginEmail == null) {
            throw new IllegalArgumentException(USER_NOT_FOUND);
        }

        // 요청된 유저와 동일한 유저인지 확인
        boolean isSameUser = loginEmail.equals(userId);

        // 해당 유저의 모든 리뷰 조회
        List<Review> reviews = reviewRepository.findAllByMember_loginEmail(loginEmail);

        // 리뷰 공개 범위에 따라 필터링
        List<ReviewDetailResponse> reviewDetailResponses = reviews.stream()
                .filter(review -> isSameUser || review.getPrivacy() == ReviewPrivacy.PUBLIC)
                .map(ReviewDetailResponse::from)
                .collect(Collectors.toList());

        // ReviewsResponse 객체에 변환된 리뷰 목록을 설정
        ReviewsResponse reviewsResponse = new ReviewsResponse();
        reviewsResponse.setReviews(reviewDetailResponses);

        return reviewsResponse;
    }

    // userId를 이용하여 Member 엔티티 조회
    private Member findMemberByUserId(String userId) {
        return memberRepository.findByLoginEmail(userId)
                .orElseThrow(() -> new IllegalArgumentException(USER_NOT_FOUND));
    }

    // 리뷰 존재 여부 및 작성자 확인
    private Review validateReviewOwner(String reviewId, String userId) {
        // 리뷰 존재 여부 확인
        Review review = reviewRepository.findByUuid(reviewId)
                .orElseThrow(() -> new EntityNotFoundException(REVIEW_NOT_FOUND + " with id: " + reviewId));
        // userId를 이용하여 Member 엔티티 조회
        Member member = findMemberByUserId(userId);
        // 작성자 검증
        if (!review.getMember().getMemberId().equals(member.getMemberId())) {
            throw new IllegalArgumentException(NOT_USER_REVIEW);
        }
        return review;
    }

    // 리뷰 엔티티 생성
    private Review createReviewEntity(ReviewPostRequest request, Member member) {
        LocalDate visitedDate = parseVisitedDate(request.getVisitedDate());

        // Place 엔티티를 ID로 조회
        Place place = placeRepository.findByUuid(request.getPlaceId())
                .orElseThrow(() -> new IllegalArgumentException(INVALID_PLACE_ID));

        return Review.builder()
                .uuid(UUID.randomUUID().toString())
                .title(request.getTitle())
                .rating(ReviewRating.valueOf(request.getRating()))
                .content(request.getContent())
                .visitedDate(visitedDate)
                .privacy(ReviewPrivacy.valueOf(request.getPrivacy()))
                .member(member)
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

    private void handleImageUpload(List<MultipartFile> imageFiles, Review review) {
        if (imageFiles != null && !imageFiles.isEmpty()) {
            List<String> imageUrls = imageService.uploadImagesAndGetUrls(imageFiles, ImageType.REVIEW);
            List<ReviewImage> reviewImages = reviewImageService.processImages(imageUrls, review);
            updateReviewWithImages(review, reviewImages);
        }
    }

    // 리뷰에 이미지와 태그 추가 및 업데이트
    private void updateReviewWithImages(Review review, List<ReviewImage> reviewImages) {
        review.setReviewImages(reviewImages);
        reviewRepository.save(review);
        reviewImageRepository.saveAll(reviewImages);
    }

    private void updateReviewWithTags(Review review, List<ReviewTag> reviewTags) {
        review.setReviewTags(reviewTags);
        reviewRepository.save(review);
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
    private void updatePlaceMetrics(String placeUuid) {
        Double averageRating = placeRepository.findAverageRatingByPlace(placeUuid);
        Integer reviewCount = placeRepository.countByPlace(placeUuid);
        Place place = placeRepository.findByUuid(placeUuid).orElseThrow();
        place.setAvgRating(averageRating);
        place.setPostCount(reviewCount);
        placeRepository.save(place);
    }
}
