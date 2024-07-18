package jjhhyb.deepvalley.community.repository;

import jjhhyb.deepvalley.community.entity.Review;
import jjhhyb.deepvalley.community.entity.ReviewRating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, String> {
    List<Review> findAllByPlace_Uuid(String uuid);
    List<Review> findAllByMember_loginEmail(String uuid);
    Optional<Review> findByUuid(String uuid);

    Review findByRating(ReviewRating rating);

}
