package jjhhyb.deepvalley.community.service;

import jjhhyb.deepvalley.community.repository.ImageRepository;
import jjhhyb.deepvalley.community.repository.ReviewImageRepository;
import jjhhyb.deepvalley.community.entity.Image;
import jjhhyb.deepvalley.community.entity.Review;
import jjhhyb.deepvalley.community.entity.ReviewImage;
import jjhhyb.deepvalley.entityId.ReviewImageId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewImageService {
    private final ImageRepository imageRepository;
    private final ReviewImageRepository reviewImageRepository;

    // 이미지 처리 (ReviewImage 객체 리스트 생성)
    public List<ReviewImage> processImages(List<String> imageUrls, Review review) {
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

    public void updateReviewImages(Review review, List<ReviewImage> updatedImages) {
        Set<Long> updatedImageIds = updatedImages.stream()
                .map(reviewImage -> reviewImage.getId().getImageId())
                .collect(Collectors.toSet());

        // 기존 이미지 리스트와 업데이트된 이미지 IDs를 비교하여 삭제할 이미지 결정
        List<ReviewImage> existingImages = new ArrayList<>(review.getReviewImages());
        List<ReviewImage> imagesToRemove = existingImages.stream()
                .filter(existingImage -> !updatedImageIds.contains(existingImage.getId().getImageId()))
                .collect(Collectors.toList());

        // 삭제할 이미지만 연관 관계에서 제거하고, 나머지는 유지
        review.getReviewImages().removeAll(imagesToRemove);

        // 새로운 이미지를 추가
        updatedImages.forEach(newImage -> {
            if (!review.getReviewImages().contains(newImage)) {
                review.getReviewImages().add(newImage);
            }
        });

        reviewImageRepository.flush();
        reviewImageRepository.deleteAll(imagesToRemove);
    }

    // 주어진 리뷰 ID에 연결된 모든 ReviewImage 객체를 조회
    public List<ReviewImage> findByReviewId(Long reviewId) {
        return reviewImageRepository.findByReview_ReviewId(reviewId);
    }

    // 주어진 ReviewImage 리스트의 모든 이미지를 삭제
    public void deleteAll(List<ReviewImage> reviewImages) {
        reviewImageRepository.deleteAll(reviewImages);
    }
}
