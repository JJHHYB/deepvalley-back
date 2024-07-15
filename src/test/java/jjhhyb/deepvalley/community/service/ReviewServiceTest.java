package jjhhyb.deepvalley.community.service;

import jjhhyb.deepvalley.tag.ReviewTagRepository;
import jjhhyb.deepvalley.community.ImageRepository;
import jjhhyb.deepvalley.community.ReviewImageRepository;
import jjhhyb.deepvalley.community.ReviewService;
import jjhhyb.deepvalley.community.ReviewRepository;
import jjhhyb.deepvalley.community.dto.request.ReviewPostRequest;
import jjhhyb.deepvalley.community.entity.Image;
import jjhhyb.deepvalley.community.entity.Review;
import jjhhyb.deepvalley.community.entity.ReviewPrivacy;
import jjhhyb.deepvalley.community.entity.ReviewRating;
import jjhhyb.deepvalley.tag.TagRepository;
import jjhhyb.deepvalley.tag.entity.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.*;

class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private ReviewImageRepository reviewImageRepository;

    @Mock
    private ReviewTagRepository reviewTagRepository;

    @InjectMocks
    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("리뷰 작성하기")
    void testCreateReview() {
        // Given
        ReviewPostRequest request = ReviewPostRequest.builder()
                .title("Test Title")
                .rating("FIVE")
                .content("Test content")
                .visitedDate("2024-07-15T12:00:00")
                .privacy("PUBLIC")
                .valleyId("1")
                .tagNames(List.of("tag1", "tag2"))
                .imageUrls(List.of("http://example.com/image1.jpg", "http://example.com/image2.jpg"))
                .build();

        Review review = Review.builder()
                .reviewId(1L)
                .uuid("uuid")
                .title("Test Title")
                .rating(ReviewRating.FIVE)
                .content("Test content")
                .visitedDate(LocalDateTime.parse("2024-07-15T12:00:00"))
                .privacy(ReviewPrivacy.PUBLIC)
                .memberId(1L)
                .valleyId(1L)
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();

        Image image1 = new Image(1L, "http://example.com/image1.jpg");
        Image image2 = new Image(2L, "http://example.com/image2.jpg");

        Tag tag1 = new Tag(1L, "tag1");
        Tag tag2 = new Tag(2L, "tag2");

        when(reviewRepository.save(any(Review.class))).thenReturn(review);
        when(imageRepository.findByImageUrl("http://example.com/image1.jpg")).thenReturn(Optional.of(image1));
        when(imageRepository.findByImageUrl("http://example.com/image2.jpg")).thenReturn(Optional.of(image2));
        when(tagRepository.findByName("tag1")).thenReturn(Optional.of(tag1));
        when(tagRepository.findByName("tag2")).thenReturn(Optional.of(tag2));
        when(imageRepository.save(any(Image.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(tagRepository.save(any(Tag.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Review savedReview = reviewService.createReview(request, 1L);

        // Then
        assertThat(savedReview.getTitle()).isEqualTo("Test Title");
        assertThat(savedReview.getRating()).isEqualTo(ReviewRating.FIVE);
        assertThat(savedReview.getContent()).isEqualTo("Test content");
        assertThat(savedReview.getVisitedDate()).isEqualTo(LocalDateTime.parse("2024-07-15T12:00:00"));
        assertThat(savedReview.getPrivacy()).isEqualTo(ReviewPrivacy.PUBLIC);
        assertThat(savedReview.getValleyId()).isEqualTo(1L);
        // 검증
        verify(reviewRepository, times(1)).save(any(Review.class));
        verify(imageRepository, times(2)).findByImageUrl(anyString());
        verify(tagRepository, times(2)).findByName(anyString());
        verify(reviewImageRepository, times(1)).saveAll(anyList());
        verify(reviewTagRepository, times(1)).saveAll(anyList());
    }
}