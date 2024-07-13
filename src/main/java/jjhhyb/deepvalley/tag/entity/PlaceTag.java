package jjhhyb.deepvalley.tag.entity;

import jakarta.persistence.*;
import jjhhyb.deepvalley.entityId.PlaceTagId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Data
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
public class PlaceTag {

    @EmbeddedId
    private PlaceTagId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("primaryId")
    @JoinColumn(name = "place_id")
    private Place place;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("secondaryId")
    @JoinColumn(name = "tag_id")
    private Tag tag;
}
