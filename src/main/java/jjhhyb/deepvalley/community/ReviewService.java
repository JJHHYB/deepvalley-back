package jjhhyb.deepvalley.community;

import jakarta.persistence.EntityNotFoundException;
import jjhhyb.deepvalley.community.dto.response.ReviewResponse;
import jjhhyb.deepvalley.place.PlaceRepository;
import jjhhyb.deepvalley.tag.ReviewTagRepository;
import jjhhyb.deepvalley.community.dto.request.ReviewPostRequest;
import jjhhyb.deepvalley.community.entity.*;
import jjhhyb.deepvalley.entityId.ReviewImageId;
import jjhhyb.deepvalley.entityId.ReviewTagId;
import jjhhyb.deepvalley.tag.TagRepository;
import jjhhyb.deepvalley.tag.entity.ReviewTag;
import jjhhyb.deepvalley.tag.entity.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final ImageRepository imageRepository;
    private final TagRepository tagRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final ReviewTagRepository reviewTagRepository;

    @Transactional
    public ReviewResponse createReview(ReviewPostRequest request) {
        // Review 엔티티 생성
        Review review = createReviewEntity(request);

        // 생성한 Review 엔티티 데이터베이스에 저장
        Review savedReview = reviewRepository.save(review);

        // 이미지, 태그 처리
        List<ReviewImage> reviewImages = processImages(request.getImageUrls(), savedReview);
        List<ReviewTag> reviewTags = processTags(request.getTagNames(), savedReview);

        // 생성된 리뷰에 이미지와 태그를 추가하고, 업데이트된 리뷰를 데이터베이스에 저장
        updateReviewWithImagesAndTags(savedReview, reviewImages, reviewTags);

        // 응답 객체로 변환 후 반환
        return ReviewResponse.from(savedReview);
    }

    // 리뷰 엔티티 생성
    private Review createReviewEntity(ReviewPostRequest request) {
        LocalDate visitedDate = parseVisitedDate(request.getVisitedDate());
        return Review.builder()
                .uuid(UUID.randomUUID().toString())
                .title(request.getTitle())
                .rating(ReviewRating.valueOf(request.getRating()))
                .content(request.getContent())
                .visitedDate(visitedDate)
                .privacy(ReviewPrivacy.valueOf(request.getPrivacy()))
                .memberId(getCurrentMemberId())
                .placeId(1L) // 현재는 하드코딩된 장소 ID, 필요시 동적으로 업데이트
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
            throw new IllegalArgumentException("Invalid date format for visitedDate: " + visitedDateStr, e);
        }
    }

    // 이미지 처리 (ReviewImage 객체 리스트 생성)
    private List<ReviewImage> processImages(List<String> imageUrls, Review review) {
        // 각 이미지 URL에 대해 이미지가 데이터베이스에 존재하는지 확인하고, 없으면 새로 저장
        List<String> urls = imageUrls != null ? imageUrls : Collections.emptyList();
        return urls.stream()
                .map(imageUrl -> createOrUpdateImage(imageUrl, review))
                .collect(Collectors.toList());
    }

    // 이미지 URL로 Image 객체 생성 or 업데이트
    private ReviewImage createOrUpdateImage(String imageUrl, Review review) {
        // 주어진 이미지 URL에 대한 Image 객체를 조회하거나 새로 생성
        Image image = imageRepository.findByImageUrl(imageUrl);
        if (image == null) {
            image = imageRepository.save(new Image(imageUrl));
        }
        return ReviewImage.builder()
                .id(new ReviewImageId(review.getReviewId(), image.getImageId()))
                .review(review)
                .image(image)
                .build();
    }

    // 태그 처리 (ReviewTag 객체 리스트 생성)
    private List<ReviewTag> processTags(List<String> tagNames, Review review) {
        // 각 태그 이름에 대해 태그가 데이터베이스에 존재하는지 확인하고, 없으면 새로 저장
        List<String> names = tagNames != null ? tagNames : Collections.emptyList();
        return names.stream()
                .map(tagName -> createOrUpdateTag(tagName, review))
                .collect(Collectors.toList());
    }

    // 태그 이름으로 Tag 객체 생성 or 업데이트
    private ReviewTag createOrUpdateTag(String tagName, Review review) {
        // 주어진 태그 이름에 대한 Tag 객체를 조회하거나 새로 생성
        Tag tag = tagRepository.findByName(tagName);
        if (tag == null) {
            tag = tagRepository.save(new Tag(tagName));
        }
        return ReviewTag.builder()
                .id(new ReviewTagId(review.getReviewId(), tag.getTagId()))
                .review(review)
                .tag(tag)
                .build();
    }

    // 리뷰에 이미지와 태그 추가 및 업데이트
    private void updateReviewWithImagesAndTags(Review review, List<ReviewImage> reviewImages, List<ReviewTag> reviewTags) {
        review.setReviewImages(reviewImages);
        review.setReviewTags(reviewTags);
        reviewRepository.save(review);
        reviewImageRepository.saveAll(reviewImages);
        reviewTagRepository.saveAll(reviewTags);
    }

    // 리뷰 업데이트
    @Transactional
    public ReviewResponse updateReview(Long reviewId, ReviewPostRequest request) {
        // 리뷰 존재 여부 조회
        Review existingReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("Review not found with id: " + reviewId));

        // 리뷰 엔티티 업데이트
        updateReviewEntity(existingReview, request);

        // 이미지, 태그 처리
        List<ReviewImage> updatedReviewImages = processImages(request.getImageUrls(), existingReview);
        List<ReviewTag> updatedReviewTags = processTags(request.getTagNames(), existingReview);

        // 기존의 이미지, 태그 제거 및 업데이트
        updateReviewImages(existingReview, updatedReviewImages);
        updateReviewTags(existingReview, updatedReviewTags);

        reviewRepository.save(existingReview);

        return ReviewResponse.from(existingReview);
    }

    private void updateReviewEntity(Review review, ReviewPostRequest request) {
        review.setTitle(request.getTitle());
        review.setRating(ReviewRating.valueOf(request.getRating()));
        review.setContent(request.getContent());
        review.setVisitedDate(parseVisitedDate(request.getVisitedDate()));
        review.setPrivacy(ReviewPrivacy.valueOf(request.getPrivacy()));
        review.setUpdatedDate(LocalDateTime.now());
    }

    private void updateReviewImages(Review review, List<ReviewImage> updatedImages) {
        Set<Long> updatedImageIds = updatedImages.stream()
                .map(reviewImage -> reviewImage.getId().getImageId())
                .collect(Collectors.toSet());

        // 기존 이미지 리스트와 업데이트된 이미지 IDs를 비교하여 삭제할 이미지들을 결정
        List<ReviewImage> existingImages = new ArrayList<>(review.getReviewImages());
        existingImages.removeIf(existingImage -> !updatedImageIds.contains(existingImage.getId().getImageId()));

        // 기존 이미지를 리뷰와의 연관관계에서 제거
        review.getReviewImages().removeAll(existingImages);
        reviewImageRepository.flush();

        review.getReviewImages().clear();
        review.getReviewImages().addAll(updatedImages);

        reviewImageRepository.deleteAll(existingImages);
    }

    private void updateReviewTags(Review review, List<ReviewTag> updatedTags) {
        Set<Long> updatedTagIds = updatedTags.stream()
                .map(reviewTag -> reviewTag.getId().getTagId())
                .collect(Collectors.toSet());

        // 기존 태그 리스트와 업데이트된 태그 IDs를 비교하여 삭제할 태그들을 결정
        List<ReviewTag> existingTags = new ArrayList<>(review.getReviewTags());
        existingTags.removeIf(existingTag -> !updatedTagIds.contains(existingTag.getId().getTagId()));

        // 기존 태그를 리뷰와의 연관관계에서 제거
        review.getReviewTags().removeAll(existingTags);
        reviewTagRepository.flush();

        review.getReviewTags().clear();
        review.getReviewTags().addAll(updatedTags);

        reviewTagRepository.deleteAll(existingTags);
    }

    private Long getCurrentMemberId() {
        // TODO : 현재 사용자의 memberId를 가져오는 메서드 구현 필요
        // 임시 값
        return 1L;
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        // 리뷰 존재 여부 조회
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("Review not found with id: " + reviewId));

        // 리뷰와 연관된 모든 이미지 삭제
        List<ReviewImage> reviewImages = reviewImageRepository.findByReview_ReviewId(review.getReviewId());
        reviewImageRepository.deleteAll(reviewImages);

        // 리뷰와 연관된 모든 태그 삭제
        List<ReviewTag> reviewTags = reviewTagRepository.findByReview_ReviewId(review.getReviewId());
        reviewTagRepository.deleteAll(reviewTags);

        // 리뷰 삭제
        reviewRepository.delete(review);
    }
}
