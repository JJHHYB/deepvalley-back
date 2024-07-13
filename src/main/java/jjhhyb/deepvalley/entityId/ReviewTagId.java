package jjhhyb.deepvalley.entityId;

import jakarta.persistence.Embeddable;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Embeddable
public class ReviewTagId extends BaseId<ReviewTagId> {
    public ReviewTagId(Long reviewId, Long tagId) {
        super(reviewId, tagId);
    }

    public Long getReviewId() {
        return getPrimaryId();
    }

    public void setReviewId(Long reviewId) {
        setPrimaryId(reviewId);
    }

    public Long getTagId() {
        return getSecondaryId();
    }

    public void setTagId(Long tagId) {
        setSecondaryId(tagId);
    }
}
