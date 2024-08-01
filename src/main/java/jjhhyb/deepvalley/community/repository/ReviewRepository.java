package jjhhyb.deepvalley.community.repository;

import jjhhyb.deepvalley.community.entity.Review;
import jjhhyb.deepvalley.user.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, String> {
    List<Review> findAllByPlace_Uuid(String uuid);
    List<Review> findAllByMember_loginEmail(String uuid);
    Optional<Review> findByUuid(String uuid);
    List<Review> findByVisitedDateAfter(LocalDate date);
    void deleteByMember(Member member);
}
