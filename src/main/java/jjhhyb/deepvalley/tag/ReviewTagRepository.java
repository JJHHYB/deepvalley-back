package jjhhyb.deepvalley.tag;

import jjhhyb.deepvalley.entityId.ReviewTagId;
import jjhhyb.deepvalley.tag.entity.ReviewTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewTagRepository extends JpaRepository<ReviewTag, ReviewTagId> {
}
