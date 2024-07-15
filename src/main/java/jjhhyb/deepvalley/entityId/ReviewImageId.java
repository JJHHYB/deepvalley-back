package jjhhyb.deepvalley.entityId;

import jakarta.persistence.Embeddable;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Embeddable
public class ReviewImageId extends BaseId<ReviewImageId> {
    public ReviewImageId(Long reviewId, Long imageId) {
        super(reviewId, imageId);
    }

    public Long getReviewId() {
        return getPrimaryId();
    }

    public void setReviewId(Long reviewId) {
        setPrimaryId(reviewId);
    }

    public Long getImageId() {
        return getSecondaryId();
    }

    public void setImageId(Long imageId) {
        setSecondaryId(imageId);
    }
}
