package jjhhyb.deepvalley.community.entity;

import jakarta.persistence.*;
import jjhhyb.deepvalley.image.Image;
import jjhhyb.deepvalley.entityId.ReviewImageId;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Builder
public class ReviewImage {

    @EmbeddedId
    private ReviewImageId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("reviewId")
    @JoinColumn(name = "review_id")
    private Review review;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("imageId")
    @JoinColumn(name = "image_id")
    private Image image;
}