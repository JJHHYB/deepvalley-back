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
import jjhhyb.deepvalley.image.ImageService;
import jjhhyb.deepvalley.image.ImageType;
import jjhhyb.deepvalley.place.Place;
import jjhhyb.deepvalley.place.PlaceRepository;
import jjhhyb.deepvalley.tag.ReviewTagRepository;
import jjhhyb.deepvalley.testObject.TestObjectMember;
import jjhhyb.deepvalley.testObject.TestObjectPlace;
import jjhhyb.deepvalley.user.entity.Member;
import jjhhyb.deepvalley.user.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
    private ImageService imageService;

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
    public void testCreateReviewWithExistingMemberAndPlace() {

        // Given: 테스트를 위한 데이터 설정
        String userId = "existingUser";
        String placeId = "existingPlaceId";

        ReviewPostRequest request = ReviewPostRequest.builder()
                .title("Great place!")
                .rating("FIVE")
                .content("Loved it!")
                .visitedDate("2024-08-01")
                .privacy("PUBLIC")
                .placeId(placeId)
                .tagNames(Arrays.asList("fun", "relaxing"))
                .build();

        List<MultipartFile> imageFiles = Arrays.asList(
                new MockMultipartFile("file1", "file1.jpg", "image/jpeg", "file1 content".getBytes()),
                new MockMultipartFile("file2", "file2.jpg", "image/jpeg", "file2 content".getBytes())
        );

        Member existingMember = TestObjectMember.createMemberWithUuid(userId);
        Place existingPlace = TestObjectPlace.createPlaceWithUuid(placeId);


        Review review = Review.builder()
                .reviewId(1L)
                .member(existingMember)
                .place(existingPlace)
                .title(request.getTitle())
                .rating(ReviewRating.valueOf(request.getRating()))
                .content(request.getContent())
                .visitedDate(LocalDate.parse(request.getVisitedDate()))
                .privacy(ReviewPrivacy.valueOf(request.getPrivacy()))
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();

        // When: Mocking 데이터 설정
        when(memberRepository.findByLoginEmail(userId)).thenReturn(Optional.of(existingMember));
        when(placeRepository.findByUuid(placeId)).thenReturn(Optional.of(existingPlace));
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        // 이미지 업로드 및 처리 Mock 설정
        List<String> imageUrls = Arrays.asList("http://image.url/1", "http://image.url/2");
        when(imageService.uploadImagesAndGetUrls(imageFiles, ImageType.REVIEW)).thenReturn(imageUrls);
        when(reviewImageService.processImages(imageUrls, review)).thenReturn(new ArrayList<>());

        // 태그 처리 Mock 설정
        when(reviewTagService.processTags(request.getTagNames(), review)).thenReturn(new ArrayList<>());

        // When: 리뷰 생성 서비스 호출
        ReviewDetailResponse response = reviewService.createReview(request, imageFiles, userId);

        // Then
        assertNotNull(response);
        verify(reviewRepository, times(3)).save(any(Review.class));
        verify(reviewImageService).processImages(imageUrls, review);
        verify(reviewTagService).processTags(request.getTagNames(), review);

        // Log results
        log.info("Created ReviewResponse");
        log.info("title: {}", response.getTitle());
    }

    @Test
    @DisplayName("[Update] 사용자 리뷰 수정")
    public void testUpdateReviewWithExistingMemberAndPlace() {

        // Given: 테스트를 위한 데이터 설정
        String reviewUUID = "testReview";
        String userId = "existingUser";
        String placeId = "existingPlaceId";

        ReviewPostRequest request = ReviewPostRequest.builder()
                .title("Updated title!")
                .rating("FOUR")
                .content("Updated content!")
                .visitedDate("2024-08-02")
                .privacy("PRIVATE")
                .placeId(placeId)
                .tagNames(Arrays.asList("exciting", "cozy"))
                .build();

        List<MultipartFile> imageFiles = new ArrayList<>(); // 이미지 파일 없음

        Member existingMember = TestObjectMember.createMemberWithUuid(userId);
        Place existingPlace = TestObjectPlace.createPlaceWithUuid(placeId);

        Review existingReview = Review.builder()
                .reviewId(1L)
                .uuid(reviewUUID)
                .member(existingMember)
                .place(existingPlace)
                .title("Original title")
                .rating(ReviewRating.FIVE)
                .content("Original content")
                .visitedDate(LocalDate.parse("2024-08-01"))
                .privacy(ReviewPrivacy.PUBLIC)
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .reviewImages(new ArrayList<>())
                .reviewTags(new ArrayList<>())
                .build();

        Review updatedReview = Review.builder()
                .reviewId(1L)
                .uuid(reviewUUID)
                .member(existingMember)
                .place(existingPlace)
                .title(request.getTitle())
                .rating(ReviewRating.valueOf(request.getRating()))
                .content(request.getContent())
                .visitedDate(LocalDate.parse(request.getVisitedDate()))
                .privacy(ReviewPrivacy.valueOf(request.getPrivacy()))
                .createdDate(existingReview.getCreatedDate())
                .updatedDate(LocalDateTime.now()) // Updated date should be current time
                .reviewImages(new ArrayList<>()) // No images to update
                .reviewTags(new ArrayList<>()) // No tags to update
                .build();

        // When: Mocking 데이터 설정
        when(reviewRepository.findByUuid(reviewUUID)).thenReturn(Optional.of(existingReview));
        when(memberRepository.findByLoginEmail(userId)).thenReturn(Optional.of(existingMember));
        when(placeRepository.findByUuid(placeId)).thenReturn(Optional.of(existingPlace));
        when(reviewRepository.save(any(Review.class))).thenReturn(updatedReview);

        // When: 리뷰 수정 서비스 호출
        ReviewDetailResponse response = reviewService.updateReview(reviewUUID, request, imageFiles, userId);

        // Then
        assertNotNull(response);
        verify(reviewRepository).save(any(Review.class)); // Save method should be called once

        // Log results
        log.info("Updated ReviewResponse");
        log.info("Review ID: {}", response.getReviewId());
        log.info("Title: {}", response.getTitle());
    }

    @Test
    @DisplayName("[Delete] 사용자 리뷰 삭제")
    public void testDeleteReviewWithExistingMember() {

        // Given: 테스트를 위한 데이터 설정
        String reviewId = "testReview";
        String userId = "existingUser";

        Member existingMember = TestObjectMember.createMemberWithUuid(userId);
        Place existingPlace = TestObjectPlace.createPlaceWithUuid("existingPlaceId");

        Review existingReview = Review.builder()
                .reviewId(1L)
                .uuid(reviewId)
                .member(existingMember)
                .place(existingPlace)
                .title("Review Title")
                .rating(ReviewRating.FIVE)
                .content("Review Content")
                .visitedDate(LocalDate.parse("2024-08-01"))
                .privacy(ReviewPrivacy.PUBLIC)
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();

        // Mock the repository and service calls
        when(reviewRepository.findByUuid(reviewId)).thenReturn(Optional.of(existingReview));
        when(memberRepository.findByLoginEmail(userId)).thenReturn(Optional.of(existingMember));

        doNothing().when(reviewRepository).delete(any(Review.class));
        doNothing().when(reviewImageService).deleteAll(anyList());
        doNothing().when(reviewTagService).deleteAll(anyList());

        // When: 리뷰 삭제 서비스 호출
        reviewService.deleteReview(reviewId, userId);

        // Then
        verify(reviewRepository).findByUuid(reviewId); // 리뷰 조회
        verify(reviewRepository).delete(existingReview); // 리뷰 삭제

        // Log results
        log.info("Review with ID : {}", reviewId);
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
        log.info("placeID({})에 대한 이미지 : {}", placeId, responses.get(0).getImageUrls());

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
        log.info("placeID({})에 대한 리뷰 : {}", placeId, response.getReviews().get(0).getTitle());

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
                .title("Test Title~~")
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
        log.info("reviewID({})에 대한 상세 조회 : {}", reviewId, response.getTitle());

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
