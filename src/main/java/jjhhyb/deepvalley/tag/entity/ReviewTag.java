package jjhhyb.deepvalley.tag.entity;

import jakarta.persistence.*;
import jjhhyb.deepvalley.community.entity.Review;
import jjhhyb.deepvalley.entityId.ReviewTagId;
import jjhhyb.deepvalley.tag.entity.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewTag {

    @EmbeddedId
    private ReviewTagId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("reviewId")
    @JoinColumn(name = "review_id")
    private Review review;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("tagId")
    @JoinColumn(name = "tag_id")
    private Tag tag;
}
