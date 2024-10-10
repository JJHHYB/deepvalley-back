package jjhhyb.deepvalley.community.repository;

import jjhhyb.deepvalley.community.entity.ReviewImage;
import jjhhyb.deepvalley.entityId.ReviewImageId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewImageRepository extends JpaRepository<ReviewImage, ReviewImageId> {
    List<ReviewImage> findByReview_ReviewId(Long reviewId);
    List<ReviewImage> findByImage_ImageUrlIn(List<String> imageUrls);
}
