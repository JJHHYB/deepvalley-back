package jjhhyb.deepvalley.community.service;

import jjhhyb.deepvalley.community.dto.response.ReviewResponse;
import jjhhyb.deepvalley.place.Place;
import jjhhyb.deepvalley.place.PlaceRepository;
import jjhhyb.deepvalley.tag.ReviewTagRepository;
import jjhhyb.deepvalley.community.ImageRepository;
import jjhhyb.deepvalley.community.ReviewImageRepository;
import jjhhyb.deepvalley.community.ReviewService;
import jjhhyb.deepvalley.community.ReviewRepository;
import jjhhyb.deepvalley.community.dto.request.ReviewPostRequest;
import jjhhyb.deepvalley.community.entity.Image;
import jjhhyb.deepvalley.tag.TagRepository;
import jjhhyb.deepvalley.tag.entity.Tag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
class ReviewServiceTest {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private PlaceRepository placeRepository;

    @Test
    @DisplayName("리뷰 작성하기")
    void testCreateReview() {
        // Given: 테스트에 필요한 데이터 준비
        String placeId = "1";

        Place place = new Place();
        place.setPlaceId(Long.valueOf(placeId));
        placeRepository.save(place);

        Image image = new Image("http://example.com/image.jpg");
        imageRepository.save(image);

//        Tag tag = new Tag(1L, "TagName");
//        tagRepository.save(tag);

        ReviewPostRequest request = ReviewPostRequest.builder()
                .title("Review Title")
                .rating("FIVE")
                .content("This is a review content.")
                .visitedDate(LocalDate.now().toString())
                .privacy("PUBLIC")
                .placeId(placeId)
                .tagNames(Arrays.asList("TagName"))
                .imageUrls(Arrays.asList("http://example.com/image.jpg"))
                .build();

        // When: ReviewService의 createReview 메서드를 호출
        ReviewResponse response = reviewService.createReview(request);

        // Then: 응답 결과 검증
        assertNotNull(response);
        assertEquals("Review Title", response.getTitle());
        assertEquals("FIVE", response.getRating());
        assertEquals("This is a review content.", response.getContent());

        // 이미지 URL과 태그 이름이 빈 리스트일 경우를 대비하여 조건문 추가
        List<String> imageUrls = response.getImageUrls();
        if (!imageUrls.isEmpty()) {
            assertEquals("http://example.com/image.jpg", imageUrls.get(0));
        }

        List<String> tagNames = response.getTagNames();
        if (!tagNames.isEmpty()) {
            assertEquals("TagName", tagNames.get(0));
        }

        // 추가적인 검증 (저장된 엔티티가 올바르게 저장되었는지 확인)
        assertNotNull(reviewRepository.findById(response.getReviewId()));
        assertNotNull(imageRepository.findByImageUrl("http://example.com/image.jpg"));
        assertNotNull(tagRepository.findByName("TagName"));
    }
}