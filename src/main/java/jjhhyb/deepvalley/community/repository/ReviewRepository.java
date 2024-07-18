package jjhhyb.deepvalley.community.repository;

import jjhhyb.deepvalley.community.entity.Review;
import jjhhyb.deepvalley.community.entity.ReviewRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, String> {
    List<Review> findAllByPlace_Uuid(String uuid);
    Optional<Review> findByUuid(String uuid);

    Review findByRating(ReviewRating rating);


}
