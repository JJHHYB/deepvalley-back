package jjhhyb.deepvalley.community;

import jjhhyb.deepvalley.community.dto.request.ReviewPostRequest;
import jjhhyb.deepvalley.community.dto.response.PlaceImageResponse;
import jjhhyb.deepvalley.community.dto.response.ReviewDetailResponse;
import jjhhyb.deepvalley.community.dto.response.ReviewsResponse;
import jjhhyb.deepvalley.community.entity.*;
import jjhhyb.deepvalley.community.repository.ReviewImageRepository;
import jjhhyb.deepvalley.community.repository.ReviewRepository;
import jjhhyb.deepvalley.community.service.ReviewImageService;
import jjhhyb.deepvalley.community.service.ReviewService;
import jjhhyb.deepvalley.community.service.ReviewTagService;
import jjhhyb.deepvalley.place.Place;
import jjhhyb.deepvalley.place.PlaceRepository;
import jjhhyb.deepvalley.tag.ReviewTagRepository;
import jjhhyb.deepvalley.user.entity.Member;
import jjhhyb.deepvalley.user.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
public class ReviewServiceTest {

    private static final Logger log = LoggerFactory.getLogger(ReviewServiceTest.class);

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ReviewImageService reviewImageService;

    @Mock
    private ReviewTagService reviewTagService;

    @Mock
    private ReviewImageRepository reviewImageRepository;

    @Mock
    private ReviewTagRepository reviewTagRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PlaceRepository placeRepository;

    @InjectMocks
    private ReviewService reviewService;

    @Test
    @DisplayName("[Create] 사용자 리뷰 작성")
    void createReviewTest() {
        // Given
        String userId = "test@example.com";
        ReviewPostRequest request = ReviewPostRequest.builder()
                .title("Test Title")
                .rating("FIVE")
                .content("Test Content")
                .visitedDate("2023-07-18")
                .privacy("PUBLIC")
                .placeId("123")
                .tagNames(List.of("캠핑", "주차가능"))
                .build();

        Member member = new Member();
        member.setMemberId(1L);

        Place place = Place.builder().placeId(1L).build();

        Review review = Review.builder()
                .uuid(UUID.randomUUID().toString())
                .title(request.getTitle())
                .rating(ReviewRating.FIVE)
                .content(request.getContent())
                .visitedDate(LocalDate.parse(request.getVisitedDate()))
                .privacy(ReviewPrivacy.PUBLIC)
                .member(member)
                .place(place)
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();

        // Mockito를 사용하여 의존성 주입 객체의 동작을 설정
        // memberRepository에서 userId로 사용자를 찾을 때, 설정한 member를 반환
        when(memberRepository.findByLoginEmail(userId)).thenReturn(Optional.of(member));
        // placeRepository에서 ID로 장소를 찾을 때, 설정한 place를 반환
        when(placeRepository.findById(anyLong())).thenReturn(Optional.of(place));
        // reviewRepository에서 Review 객체를 저장할 때, 설정한 review를 반환
        when(reviewRepository.save(any(Review.class))).thenReturn(review);
        // reviewImageService에서 이미지 처리 요청 시 빈 리스트를 반환
        when(reviewImageService.processImages(anyList(), any(Review.class))).thenReturn(Collections.emptyList());
        // reviewTagService에서 태그 처리 요청 시 빈 리스트를 반환
        when(reviewTagService.processTags(anyList(), any(Review.class))).thenReturn(Collections.emptyList());

        // When
        // ReviewService의 createReview 메서드를 호출하여 실제 리뷰 생성 작업 수행
//        ReviewDetailResponse response = reviewService.createReview(request, userId);

        // Log results
//        log.info("Created ReviewResponse: {}", response);
//        log.info("Title: {}", response.getTitle());
//        log.info("Rating: {}", response.getRating());
//        log.info("Content: {}", response.getContent());
//        log.info("Visited Date: {}", response.getVisitedDate());
//        log.info("Privacy: {}", response.getPrivacy());
//
//        // Then
//        assertThat(response).isNotNull();
//        assertThat(response.getTitle()).isEqualTo(request.getTitle());
//        assertThat(response.getRating()).isEqualTo(request.getRating());
//        assertThat(response.getContent()).isEqualTo(request.getContent());
//        assertThat(response.getVisitedDate()).isEqualTo(request.getVisitedDate());
//        assertThat(response.getPrivacy()).isEqualTo(request.getPrivacy());
    }

    @Test
    @DisplayName("[Update] 사용자 리뷰 수정")
    void updateReviewTest() {

        // Given
        String reviewId = "1L";
        String userId = "test@example.com";
        ReviewPostRequest request = ReviewPostRequest.builder()
                .title("Update Title")
                .rating("FOUR")
                .content("Update Content")
                .visitedDate("2023-07-18")
                .privacy("PUBLIC")
                .build();

        Member member = new Member();
        member.setMemberId(1L);

        Review review = Review.builder()
                .uuid(reviewId)
                .title("Test Title")
                .rating(ReviewRating.FIVE)
                .content("Test Content")
                .visitedDate(LocalDate.now())
                .privacy(ReviewPrivacy.PUBLIC)
                .member(member)
                .place(new Place())
                .reviewImages(new ArrayList<>())
                .reviewTags(new ArrayList<>())
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();

        when(memberRepository.findByLoginEmail(userId)).thenReturn(Optional.of(member));
        when(reviewRepository.save(any(Review.class))).thenReturn(review);
        when(reviewImageService.processImages(anyList(), any(Review.class))).thenReturn(Collections.emptyList());
        when(reviewTagService.processTags(anyList(), any(Review.class))).thenReturn(Collections.emptyList());

        // When
//        ReviewDetailResponse response = reviewService.updateReview(reviewId, request, userId);
//
//        // Log results
//        log.info("ReviewResponse: {}", response);
//        log.info("Title: {}", response.getTitle());
//        log.info("Rating: {}", response.getRating());
//        log.info("Content: {}", response.getContent());
//
//        // Then
//        assertThat(response).isNotNull();
//        assertThat(response.getTitle()).isEqualTo(request.getTitle());
//        assertThat(response.getRating()).isEqualTo(request.getRating());
    }

    @Test
    @DisplayName("[Delete] 사용자 리뷰 삭제")
    void deleteReviewTest() {
        // Given
        String reviewId = "1L";
        String userId = "test@example.com";

        Member member = new Member();
        member.setMemberId(1L);

        Review review = Review.builder()
                .uuid(reviewId)
                .title("Test Title")
                .rating(ReviewRating.FIVE)
                .content("Test Content")
                .visitedDate(LocalDate.now())
                .privacy(ReviewPrivacy.PUBLIC)
                .member(member)
                .place(new Place())
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();

        when(memberRepository.findByLoginEmail(userId)).thenReturn(Optional.of(member));

        // When
        reviewService.deleteReview(reviewId, userId);

        // Log results
        log.info("삭제된 ReviewID : {}", reviewId);

        // Then
        verify(reviewRepository, times(1)).delete(review);
        verify(reviewImageService, times(1)).deleteAll(anyList());
        verify(reviewTagService, times(1)).deleteAll(anyList());
    }

    @Test
    @DisplayName("[Get] 장소에 대한 사진 리스트 조회")
    void searchReviewImageTest() {
        // Given
        String placeId = "1L";
        Review review = new Review();
        review.setReviewId(1L);
        ReviewImage reviewImage = new ReviewImage();
        reviewImage.setImage(new Image("http://example.com/image.jpg"));

        when(reviewRepository.findAllByPlace_Uuid(placeId)).thenReturn(Collections.singletonList(review));
        when(reviewImageRepository.findByReview_ReviewId(review.getReviewId())).thenReturn(Collections.singletonList(reviewImage));

        // When
        List<PlaceImageResponse> responses = reviewService.searchReviewImage(placeId);

        //  Log results
        log.info("placeID({})에 대한 이미지 : {}", placeId, responses);

        // Then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getImageUrls()).contains("http://example.com/image.jpg");
    }

    @Test
    @DisplayName("[Get] 장소에 대한 리뷰 리스트 조회")
    void getPlaceReviewsTest() {
        // Given
        String placeId = "1L";
        Member member = new Member();
        Review review = Review.builder()
                .reviewId(1L)
                .title("Test Title")
                .rating(ReviewRating.FIVE)
                .content("Test Content")
                .visitedDate(LocalDate.now())
                .privacy(ReviewPrivacy.PUBLIC)
                .member(member)
                .place(new Place())
                .reviewImages(new ArrayList<>())
                .reviewTags(new ArrayList<>())
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();

        // Mockito 설정
        when(reviewRepository.findAllByPlace_Uuid(placeId)).thenReturn(Collections.singletonList(review));

        // When
        ReviewsResponse response = reviewService.getPlaceReviews(placeId);

        // Log results
        log.info("placeID({})에 대한 리뷰 : {}", placeId, response);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getReviews()).hasSize(1);
        // 추가 검증: 응답의 리뷰 목록에서 리뷰의 제목과 평점도 검증
        ReviewDetailResponse reviewDetailResponse = response.getReviews().get(0);
        assertThat(reviewDetailResponse.getTitle()).isEqualTo(review.getTitle());
        assertThat(reviewDetailResponse.getRating()).isEqualTo(review.getRating().name());
    }

    @Test
    @DisplayName("[Get] 리뷰 상세 조회")
    void getReviewDetailTest() {
        // Given
        String reviewId = "1L";
        Member member = new Member();
        Review review = Review.builder()
                .reviewId(1L)
                .title("Test Title")
                .rating(ReviewRating.FIVE)
                .content("Test Content")
                .visitedDate(LocalDate.now())
                .privacy(ReviewPrivacy.PUBLIC)
                .member(member)
                .place(new Place())
                .reviewImages(new ArrayList<>())
                .reviewTags(new ArrayList<>())
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();

        // When
        ReviewDetailResponse response = reviewService.getReviewDetail(reviewId);

        //Log results
        log.info("reviewID({})에 대한 상세 조회 : {}", reviewId, response);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getReviewId()).isEqualTo(reviewId);
    }

    @Test
    @DisplayName("[Get] 특정 유저의 리뷰 목록 조회 - 자신의 리뷰 반환")
    void getSpecificMemberReviews_OwnReviews() {
        // Given
        String loginEmail = "test@example.com";
        String userId = "test@example.com";

        Member member = new Member();
        member.setLoginEmail(loginEmail);

        Place place = Place.builder().placeId(1L).build();

        Review review1 = Review.builder()
                .uuid(UUID.randomUUID().toString())
                .title("Review 1")
                .rating(ReviewRating.FIVE)
                .content("Content 1")
                .privacy(ReviewPrivacy.PUBLIC)
                .member(member)
                .place(place)
                .reviewImages(new ArrayList<>())
                .reviewTags(new ArrayList<>())
                .build();

        Review review2 = Review.builder()
                .uuid(UUID.randomUUID().toString())
                .title("Review 2")
                .rating(ReviewRating.THREE)
                .content("Content 2")
                .privacy(ReviewPrivacy.PRIVATE)
                .member(member)
                .place(place)
                .reviewImages(new ArrayList<>())
                .reviewTags(new ArrayList<>())
                .build();


        List<Review> reviews = Arrays.asList(review1, review2);

        // Mockito를 사용하여 reviewRepository에서 이메일로 리뷰 목록을 조회할 때, 설정한 리뷰 목록을 반환
        when(reviewRepository.findAllByMember_loginEmail(loginEmail)).thenReturn(reviews);

        // When
        // ReviewService의 getSpecificMemberReviews 메서드를 호출하여 리뷰 목록 조회 작업 수행
        ReviewsResponse response = reviewService.getSpecificMemberReviews(loginEmail, userId);

        // Log results
        log.info("loginEmail({})에 대한 리뷰 : {}", loginEmail, response.getReviews());

        // Then
        // 반환된 리뷰 목록이 null이 아닌지 확인
        assertThat(response).isNotNull();
        // 반환된 리뷰 목록의 크기가 예상된 값과 일치하는지 확인 (자신의 리뷰이므로 모든 리뷰가 반환됨)
        assertThat(response.getReviews()).hasSize(2);
        // 각 리뷰의 내용을 검증
        assertThat(response.getReviews().get(0).getTitle()).isEqualTo("Review 1");
        assertThat(response.getReviews().get(0).getContent()).isEqualTo("Content 1");
        assertThat(response.getReviews().get(1).getTitle()).isEqualTo("Review 2");
        assertThat(response.getReviews().get(1).getContent()).isEqualTo("Content 2");
    }
    @Test
    @DisplayName("[Get] 특정 유저의 리뷰 목록 조회 - 다른 사용자가 조회할 경우")
    void getSpecificMemberReviews_OtherUser() {
        // Given
        String loginEmail = "test@example.com";
        String otherUserId = "other@example.com";

        Member member = new Member();
        member.setLoginEmail(loginEmail);

        Place place = Place.builder().placeId(1L).build();

        Review review1 = Review.builder()
                .uuid(UUID.randomUUID().toString())
                .title("Review 1")
                .rating(ReviewRating.FIVE)
                .content("Content 1")
                .privacy(ReviewPrivacy.PUBLIC)
                .member(member)
                .place(place)
                .reviewImages(new ArrayList<>())
                .reviewTags(new ArrayList<>())
                .build();

        Review review2 = Review.builder()
                .uuid(UUID.randomUUID().toString())
                .title("Review 2")
                .rating(ReviewRating.THREE)
                .content("Content 2")
                .privacy(ReviewPrivacy.PRIVATE)
                .member(member)
                .place(place)
                .reviewImages(new ArrayList<>())
                .reviewTags(new ArrayList<>())
                .build();

        List<Review> reviews = Arrays.asList(review1, review2);

        // Mockito를 사용하여 reviewRepository에서 이메일로 리뷰 목록을 조회할 때, 설정한 리뷰 목록을 반환
        when(reviewRepository.findAllByMember_loginEmail(loginEmail)).thenReturn(reviews);

        // When
        // ReviewService의 getSpecificMemberReviews 메서드를 호출하여 리뷰 목록 조회 작업 수행
        ReviewsResponse responseForOtherUser = reviewService.getSpecificMemberReviews(loginEmail, otherUserId);

        // Log results
        log.info("loginEmail({})에 대한 리뷰 : {}", loginEmail, responseForOtherUser);

        // Then
        // 공개된 리뷰만 포함되어 있는지 확인
        assertThat(responseForOtherUser).isNotNull();
        assertThat(responseForOtherUser.getReviews()).hasSize(1);
        assertThat(responseForOtherUser.getReviews().get(0).getTitle()).isEqualTo("Review 1");
        assertThat(responseForOtherUser.getReviews().get(0).getContent()).isEqualTo("Content 1");
    }
}
