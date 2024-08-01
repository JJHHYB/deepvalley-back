package jjhhyb.deepvalley.recommend;

import jjhhyb.deepvalley.community.entity.Review;
import jjhhyb.deepvalley.community.entity.ReviewPrivacy;
import jjhhyb.deepvalley.community.entity.ReviewRating;
import jjhhyb.deepvalley.community.repository.ReviewRepository;
import jjhhyb.deepvalley.place.Place;
import jjhhyb.deepvalley.recommend.dto.response.RecommendResponse;
import jjhhyb.deepvalley.tag.entity.ReviewTag;
import jjhhyb.deepvalley.tag.entity.Tag;
import jjhhyb.deepvalley.testObject.TestObjectMember;
import jjhhyb.deepvalley.testObject.TestObjectPlace;
import jjhhyb.deepvalley.user.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class RecommendServiceTest {
    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private RecommendService recommendService;

    @Test
    @DisplayName("[GET] 추천 계곡 조회")
    public void testGetRecommendPlace() {
        // given
        LocalDate oneWeekAgo = LocalDate.now().minusWeeks(1);

        String userId = "existingUser";
        String placeId = "existingPlaceId";
        String placeName = "Valley A";

        Member existingMember = TestObjectMember.createMemberWithUuid(userId);
        Place existingPlace = TestObjectPlace.createPlaceWithUuidAndName(placeId, placeName);

        Review review1 = Review.builder()
                .reviewId(1L)
                .uuid("review1")
                .member(existingMember)
                .place(existingPlace)
                .title("Great place")
                .rating(ReviewRating.FIVE)
                .content("Amazing experience")
                .visitedDate(oneWeekAgo.plusDays(1))
                .privacy(ReviewPrivacy.PUBLIC)
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .reviewImages(new ArrayList<>())
                .build();

        Review review2 = Review.builder()
                .reviewId(2L)
                .uuid("review2")
                .member(existingMember)
                .place(existingPlace)
                .title("Nice place")
                .rating(ReviewRating.FOUR)
                .content("Pretty good")
                .visitedDate(oneWeekAgo.plusDays(2))
                .privacy(ReviewPrivacy.PUBLIC)
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .reviewImages(new ArrayList<>())
                .build();

        Review review3 = Review.builder()
                .reviewId(3L)
                .uuid("review3")
                .member(existingMember)
                .place(existingPlace)
                .title("Okay place")
                .rating(ReviewRating.THREE)
                .content("It was okay")
                .visitedDate(oneWeekAgo.plusDays(3))
                .privacy(ReviewPrivacy.PUBLIC)
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .reviewImages(new ArrayList<>())
                .build();

        // Tag 객체 생성
        Tag tag1 = new Tag("주차가능");
        Tag tag2 = new Tag("캠핑가능");
        Tag tag3 = new Tag("수영가능");

        // ReviewTag 객체 생성 및 설정
        ReviewTag reviewTag1 = new ReviewTag();
        reviewTag1.setTag(tag1);
        reviewTag1.setReview(review1);

        ReviewTag reviewTag2 = new ReviewTag();
        reviewTag2.setTag(tag2);
        reviewTag2.setReview(review2);

        ReviewTag reviewTag3 = new ReviewTag();
        reviewTag3.setTag(tag3);
        reviewTag3.setReview(review3);

        // Review 객체에 ReviewTag 추가
        review1.setReviewTags(List.of(reviewTag1));
        review2.setReviewTags(List.of(reviewTag2));
        review3.setReviewTags(List.of(reviewTag3));

        when(reviewRepository.findByVisitedDateAfter(any(LocalDate.class)))
                .thenReturn(Arrays.asList(review1, review2, review3));

        // when
        List<RecommendResponse> recommendations = recommendService.getRecommendPlace();

        // then
        assertEquals(1, recommendations.size(), "Expected only one recommendation");

        RecommendResponse response = recommendations.get(0);
        assertEquals("Valley A", response.getValleyName(), "Expected valley name to be 'Valley A'");
        assertEquals("existingPlaceId", response.getPlaceId(), "Expected place ID to be 'existingPlaceId'");
        assertEquals(Arrays.asList("주차가능", "캠핑가능", "수영가능"), response.getTagNames(), "Expected tag names to match");
    }
}
