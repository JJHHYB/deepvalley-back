package jjhhyb.deepvalley.entityId;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@EqualsAndHashCode
@Embeddable
public class ReviewImageId implements Serializable {
    @Column(name = "REVIEW_ID")
    private Long reviewId;

    @Column(name = "IMAGE_ID")
    private Long imageId;

    public ReviewImageId(Long reviewId, Long imageId) {
        this.reviewId = reviewId;
        this.imageId = imageId;
    }
}
