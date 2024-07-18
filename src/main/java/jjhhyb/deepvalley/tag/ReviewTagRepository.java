package jjhhyb.deepvalley.tag;

import jjhhyb.deepvalley.tag.entity.ReviewTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ReviewTagRepository extends JpaRepository<ReviewTag, Long> {
    // ReviewId로 ReviewTag를 조회하는 메서드
    List<ReviewTag> findByReview_ReviewId(Long reviewId);
}
