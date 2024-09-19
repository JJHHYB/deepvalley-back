package jjhhyb.deepvalley.community;

import jjhhyb.deepvalley.community.entity.Image;
import jjhhyb.deepvalley.community.entity.Review;
import jjhhyb.deepvalley.community.entity.ReviewImage;
import jjhhyb.deepvalley.community.repository.ImageRepository;
import jjhhyb.deepvalley.community.repository.ReviewImageRepository;
import jjhhyb.deepvalley.community.service.ReviewImageService;
import jjhhyb.deepvalley.entityId.ReviewImageId;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ReviewImageServiceTest {

    @MockBean
    private ImageRepository imageRepository;

    @MockBean
    private ReviewImageRepository reviewImageRepository;

    @Autowired
    private ReviewImageService reviewImageService;

    @Test
    public void testProcessImages() {
        // 테스트 데이터 설정
        Review review = new Review();
        review.setReviewId(1L);
        List<String> imageUrls = Arrays.asList("image1.jpg", "image2.jpg");

        // ImageRepository mock 동작 설정 (이미지 URL에 대한 Image 엔티티 반환)
        when(imageRepository.findByImageUrl("image1.jpg"))
                .thenReturn(null);  // 없을 경우 새로 생성
        when(imageRepository.findByImageUrl("image2.jpg"))
                .thenReturn(new Image(2L, "image2.jpg"));

        when(imageRepository.save(any(Image.class)))
                .thenAnswer(invocation -> {
                    Image image = invocation.getArgument(0);  // 전달된 Image 객체
                    image.setImageId(1L);  // imageId 설정
                    return image;
                });

        // processImages 호출
        List<ReviewImage> reviewImages = reviewImageService.processImages(imageUrls, review);

        // 결과 검증
        assertEquals(2, reviewImages.size());
        verify(imageRepository, times(1)).save(any(Image.class)); // 새 이미지가 저장되었는지 확인
    }

    @Test
    public void testUpdateReviewImages() {
        // 기존 리뷰 설정
        Review review = new Review();
        review.setReviewId(1L);

        // 기존 이미지
        ReviewImage oldImage = new ReviewImage();
        oldImage.setId(new ReviewImageId(1L, 1L)); // ReviewId, ImageId
        review.getReviewImages().add(oldImage);

        // 새로운 이미지
        ReviewImage newImage = new ReviewImage();
        newImage.setId(new ReviewImageId(1L, 2L));

        List<ReviewImage> updatedImages = Arrays.asList(newImage);

        // updateReviewImages 호출
        reviewImageService.updateReviewImages(review, updatedImages);

        // 검증
        assertTrue(review.getReviewImages().contains(newImage)); // 새로운 이미지가 추가되었는지 확인
        assertFalse(review.getReviewImages().contains(oldImage)); // 기존 이미지가 제거되었는지 확인

        verify(reviewImageRepository, times(1)).deleteAll(anyList()); // 기존 이미지 삭제 확인
    }
}
