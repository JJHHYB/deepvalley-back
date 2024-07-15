package jjhhyb.deepvalley.entityId;

import jakarta.persistence.Embeddable;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Embeddable
public class PlaceTagId extends BaseId<PlaceTagId> {
    public PlaceTagId(Long placeId, Long tagId) {
        super(placeId, tagId);
    }

    public Long getPlaceId() {
        return getPrimaryId();
    }

    public void setPlaceId(Long placeId) {
        setPrimaryId(placeId);
    }

    public Long getTagId() {
        return getSecondaryId();
    }

    public void setTagId(Long tagId) {
        setSecondaryId(tagId);
    }
}
