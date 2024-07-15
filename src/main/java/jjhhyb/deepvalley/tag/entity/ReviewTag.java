package jjhhyb.deepvalley.tag.entity;

import jakarta.persistence.*;
import jjhhyb.deepvalley.community.entity.Review;
import jjhhyb.deepvalley.entityId.ReviewTagId;
import jjhhyb.deepvalley.tag.entity.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewTag {

    @EmbeddedId
    private ReviewTagId id;

    @MapsId("reviewId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    @MapsId("tagId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;
}
