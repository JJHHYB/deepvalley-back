package jjhhyb.deepvalley.community.service;

import jjhhyb.deepvalley.community.repository.ImageRepository;
import jjhhyb.deepvalley.community.repository.ReviewImageRepository;
import jjhhyb.deepvalley.community.entity.Image;
import jjhhyb.deepvalley.community.entity.Review;
import jjhhyb.deepvalley.community.entity.ReviewImage;
import jjhhyb.deepvalley.entityId.ReviewImageId;
import jjhhyb.deepvalley.image.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewImageService {
    private final ImageRepository imageRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final ImageService imageService;

    // 이미지 URL 리스트로 ReviewImage 객체 리스트 생성
    public List<ReviewImage> processImages(List<String> imageUrls, Review review) {
        return imageUrls.stream()
                .map(imageUrl -> createOrUpdateImage(imageUrl, review))
                .collect(Collectors.toList());
    }

    // 이미지 URL로 Image 객체 생성 or 업데이트   ********************************************
    private ReviewImage createOrUpdateImage(String imageUrl, Review review) {
        // 데이터베이스에서 이미지 조회, 없으면 새로 생성
        Image image = imageRepository.findByImageUrl(imageUrl);
        if (image == null) {
            image = imageRepository.save(new Image(imageUrl));
        }
        // ReviewImage 객체 생성
        return ReviewImage.builder()
                .id(new ReviewImageId(review.getReviewId(), image.getImageId()))
                .review(review)
                .image(image)
                .build();
    }

    // 리뷰와 연결된 이미지 업데이트     *******************************************
    public void updateReviewImages(Review review, List<ReviewImage> updatedImages) {
        Set<Long> updatedImageIds = updatedImages.stream()
                .map(reviewImage -> reviewImage.getId().getImageId())
                .collect(Collectors.toSet());

        // 기존 이미지 리스트와 업데이트된 이미지 IDs를 비교하여 삭제할 이미지들을 결정
        List<ReviewImage> existingImages = new ArrayList<>(review.getReviewImages());   // 모든 기존 이미지
        List<ReviewImage> imagesToDelete = existingImages.stream()                      // 더 이상 사용되지 않는 이미지를 필터링해 포함
                .filter(existingImage -> !updatedImageIds.contains(existingImage.getId().getImageId()))
                .toList();

        // 기존 이미지를 리뷰와의 연관관계에서 제거
        review.getReviewImages().removeAll(existingImages);
        reviewImageRepository.flush();

        review.getReviewImages().clear();
        review.getReviewImages().addAll(updatedImages);

        // S3에서 이미지 삭제
        imageService.deleteImages(imagesToDelete.stream()
                .map(image -> image.getImage().getImageUrl())
                .collect(Collectors.toList()));
        // 데이터 베이스에서 삭제
        reviewImageRepository.deleteAll(existingImages);
    }

    // 주어진 ReviewImage 리스트의 모든 이미지 삭제
    public void deleteAll(List<ReviewImage> reviewImages) {
        imageService.deleteImages(reviewImages.stream()
                .map(image -> image.getImage().getImageUrl())
                .collect(Collectors.toList()));
        reviewImageRepository.deleteAll(reviewImages);
    }
}
