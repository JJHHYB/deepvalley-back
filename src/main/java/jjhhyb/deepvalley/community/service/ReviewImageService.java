package jjhhyb.deepvalley.community.service;

import jjhhyb.deepvalley.community.repository.ImageRepository;
import jjhhyb.deepvalley.community.repository.ReviewImageRepository;
import jjhhyb.deepvalley.image.Image;
import jjhhyb.deepvalley.community.entity.Review;
import jjhhyb.deepvalley.community.entity.ReviewImage;
import jjhhyb.deepvalley.entityId.ReviewImageId;
import jjhhyb.deepvalley.image.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewImageService {
    private final ImageRepository imageRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final S3Service s3Service;

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

    // 주어진 ReviewImage 리스트의 모든 이미지를 삭제
    @Transactional
    public void deleteAll(List<ReviewImage> reviewImages) {
        for (ReviewImage reviewImage : reviewImages) {
            // S3에서 이미지 삭제 (imageUrl 필드 사용)
            String imageUrl = reviewImage.getImage().getImageUrl();
            s3Service.deleteImage(imageUrl); // S3에서 이미지 삭제하는 메서드 호출

            // DB에서 ReviewImage 삭제
            reviewImageRepository.delete(reviewImage);
        }
    }
}
