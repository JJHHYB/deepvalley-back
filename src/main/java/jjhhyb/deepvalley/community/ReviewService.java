package jjhhyb.deepvalley.community;

import jjhhyb.deepvalley.tag.ReviewTagRepository;
import jjhhyb.deepvalley.community.dto.request.ReviewPostRequest;
import jjhhyb.deepvalley.community.entity.*;
import jjhhyb.deepvalley.entityId.ReviewImageId;
import jjhhyb.deepvalley.entityId.ReviewTagId;
import jjhhyb.deepvalley.tag.TagRepository;
import jjhhyb.deepvalley.tag.entity.ReviewTag;
import jjhhyb.deepvalley.tag.entity.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private ReviewImageRepository reviewImageRepository;

    @Autowired
    private ReviewTagRepository reviewTagRepository;

    @Transactional
    public Review createReview(ReviewPostRequest request, Long memberId) {
        // Review 객체 생성
        Review review = Review.builder()
                .uuid(UUID.randomUUID().toString())
                .title(request.getTitle())
                .rating(ReviewRating.valueOf(request.getRating().toUpperCase()))
                .content(request.getContent())
                .visitedDate(LocalDateTime.parse(request.getVisitedDate()))
                .privacy(ReviewPrivacy.valueOf(request.getPrivacy().toUpperCase()))
                .memberId(memberId)
                .valleyId(Long.parseLong(request.getValleyId()))
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();

        // Review 저장
        review = reviewRepository.save(review);

        processReviewImagesAndTags(review, request);

        return review;
    }

    private void processReviewImagesAndTags(Review review, ReviewPostRequest request) {
        // reviewId를 final로 선언
        final Long reviewId = review.getReviewId();

        // 이미지 처리
        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            List<ReviewImage> reviewImages = request.getImageUrls().stream()
                    .map(imageUrl -> {
                        Image image = imageRepository.findByImageUrl(imageUrl)
                                .orElseGet(() -> imageRepository.save(new Image(imageUrl)));
                        return new ReviewImage(new ReviewImageId(reviewId, image.getImageId()), review, image);
                    })
                    .collect(Collectors.toList());

            reviewImageRepository.saveAll(reviewImages);
            review.setReviewImages(reviewImages);
        }

        // 태그 처리
        if (request.getTagNames() != null && !request.getTagNames().isEmpty()) {
            List<ReviewTag> reviewTags = request.getTagNames().stream()
                    .map(tagName -> {
                        Tag tag = tagRepository.findByName(tagName)
                                .orElseGet(() -> tagRepository.save(new Tag(tagName)));
                        return new ReviewTag(new ReviewTagId(reviewId, tag.getTagId()), review, tag);
                    })
                    .collect(Collectors.toList());

            reviewTagRepository.saveAll(reviewTags);
            review.setReviewTags(reviewTags);
        }
    }
}
