package jjhhyb.deepvalley.community.repository;

import jjhhyb.deepvalley.community.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    Image findByImageUrl(String imageUrl);
}
