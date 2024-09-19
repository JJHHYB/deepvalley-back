package jjhhyb.deepvalley.testObject;

import jjhhyb.deepvalley.place.Place;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;

import java.time.LocalTime;

public class TestObjectPlace {
    static final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 0);

    public static Place createPlaceWithUuid(String uuid) {
        return Place.builder()
                .placeId(1L)
                .uuid(uuid)
                .name("Test Place")
                .address("123 Test Street")
                .thumbnail("http://example.com/thumbnail.jpg")
                .region("Test Region")
                .content("Test Content")
                .location(geometryFactory.createPoint(new Coordinate(1.5, 1.5)))
                .postCount(0)
                .avgRating(0.0)
                .build();
    }
    public static Place createPlaceWithUuidAndName(String uuid, String name) {
        return Place.builder()
                .placeId(1L)
                .uuid(uuid)
                .name(name)
                .address("123 Test Street")
                .thumbnail("http://example.com/thumbnail.jpg")
                .region("Test Region")
                .content("Test Content")
                .location(geometryFactory.createPoint(new Coordinate(1.5, 1.5)))
                .postCount(0)
                .avgRating(0.0)
                .build();
    }
}
