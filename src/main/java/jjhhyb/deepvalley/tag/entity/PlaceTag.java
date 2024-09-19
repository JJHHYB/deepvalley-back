package jjhhyb.deepvalley.tag.entity;

import jakarta.persistence.*;
import jjhhyb.deepvalley.entityId.PlaceTagId;
import jjhhyb.deepvalley.place.Place;
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
public class PlaceTag {

    @EmbeddedId
    private PlaceTagId id;

    @MapsId("placeId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    private Place place;

    @MapsId("tagId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;
}
