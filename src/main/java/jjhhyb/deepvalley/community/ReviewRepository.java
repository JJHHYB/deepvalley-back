package jjhhyb.deepvalley.community;

import jjhhyb.deepvalley.community.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

}
