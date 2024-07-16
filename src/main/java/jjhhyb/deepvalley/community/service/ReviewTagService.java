package jjhhyb.deepvalley.community.service;

import jjhhyb.deepvalley.community.entity.Review;
import jjhhyb.deepvalley.entityId.ReviewTagId;
import jjhhyb.deepvalley.tag.ReviewTagRepository;
import jjhhyb.deepvalley.tag.TagRepository;
import jjhhyb.deepvalley.tag.entity.ReviewTag;
import jjhhyb.deepvalley.tag.entity.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewTagService {
    private final TagRepository tagRepository;
    private final ReviewTagRepository reviewTagRepository;

    // 태그 처리 (ReviewTag 객체 리스트 생성)
    public List<ReviewTag> processTags(List<String> tagNames, Review review) {
        // 각 태그 이름에 대해 태그가 데이터베이스에 존재하는지 확인하고, 없으면 새로 저장
        List<String> names = tagNames != null ? tagNames : Collections.emptyList();
        return names.stream()
                .map(tagName -> createOrUpdateTag(tagName, review))
                .collect(Collectors.toList());
    }

    // 태그 이름으로 Tag 객체 생성 or 업데이트
    private ReviewTag createOrUpdateTag(String tagName, Review review) {
        // 주어진 태그 이름에 대한 Tag 객체를 조회하거나 새로 생성
        Tag tag = tagRepository.findByName(tagName);
        if (tag == null) {
            tag = tagRepository.save(new Tag(tagName));
        }
        return ReviewTag.builder()
                .id(new ReviewTagId(review.getReviewId(), tag.getTagId()))
                .review(review)
                .tag(tag)
                .build();
    }

    public void updateReviewTags(Review review, List<ReviewTag> updatedTags) {
        Set<Long> updatedTagIds = updatedTags.stream()
                .map(reviewTag -> reviewTag.getId().getTagId())
                .collect(Collectors.toSet());

        // 기존 태그 리스트와 업데이트된 태그 IDs를 비교하여 삭제할 태그들을 결정
        List<ReviewTag> existingTags = new ArrayList<>(review.getReviewTags());
        existingTags.removeIf(existingTag -> !updatedTagIds.contains(existingTag.getId().getTagId()));

        // 기존 태그를 리뷰와의 연관관계에서 제거
        review.getReviewTags().removeAll(existingTags);
        reviewTagRepository.flush();

        review.getReviewTags().clear();
        review.getReviewTags().addAll(updatedTags);

        reviewTagRepository.deleteAll(existingTags);
    }

    // 주어진 리뷰 ID에 연결된 모든 ReviewTag 객체를 조회
    public List<ReviewTag> findByReviewId(Long reviewId) {
        return reviewTagRepository.findByReview_ReviewId(reviewId);
    }

    // 주어진 ReviewTag 리스트의 모든 태그를 삭제
    public void deleteAll(List<ReviewTag> reviewTags) {
        reviewTagRepository.deleteAll(reviewTags);
    }
}
