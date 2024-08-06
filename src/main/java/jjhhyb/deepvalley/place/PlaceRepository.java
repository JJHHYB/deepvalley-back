package jjhhyb.deepvalley.place;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PlaceRepository extends JpaRepository<Place, Long> {
    Optional<Place> findByUuid(String uuid);

    @Query("SELECT AVG(CASE r.rating " +
            "WHEN jjhhyb.deepvalley.community.entity.ReviewRating.ONE THEN 1 " +
            "WHEN jjhhyb.deepvalley.community.entity.ReviewRating.TWO THEN 2 " +
            "WHEN jjhhyb.deepvalley.community.entity.ReviewRating.THREE THEN 3 " +
            "WHEN jjhhyb.deepvalley.community.entity.ReviewRating.FOUR THEN 4 " +
            "WHEN jjhhyb.deepvalley.community.entity.ReviewRating.FIVE THEN 5 " +
            "ELSE 0 " +
            "END) " +
            "FROM Review r WHERE r.place.uuid = :placeUuid")
    Double findAverageRatingByPlace(@Param("placeUuid") String placeUuid);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.place.uuid = :placeUuid")
    Integer countByPlace(@Param("placeUuid") String placeUuid);
}
