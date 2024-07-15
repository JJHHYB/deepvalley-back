package jjhhyb.deepvalley.community.entity;

import jakarta.persistence.*;
import jjhhyb.deepvalley.entityId.ReviewImageId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class ReviewImage {

    @EmbeddedId
    private ReviewImageId id;

    @MapsId("reviewId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review reviewId;

    @MapsId("imageId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id")
    private Image imageId;
}