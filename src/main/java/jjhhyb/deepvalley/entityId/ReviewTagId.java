package jjhhyb.deepvalley.entityId;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@EqualsAndHashCode
@Embeddable
@Getter
public class ReviewTagId implements Serializable {
    @Column(name = "REVIEW_ID")
    private Long reviewId;

    @Column(name = "TAG_ID")
    private Long tagId;

    public ReviewTagId(Long reviewId, Long tagId) {
        this.reviewId = reviewId;
        this.tagId = tagId;
    }
}
