package jjhhyb.deepvalley.entityId;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@EqualsAndHashCode
@Embeddable
public class PlaceTagId implements Serializable {
    @Column(name = "PLACE_ID")
    private Long placeId;

    @Column(name = "TAG_ID")
    private Long tagId;

    public PlaceTagId(Long placeId, Long tagId) {
        this.placeId = placeId;
        this.tagId = tagId;
    }
}
