package jjhhyb.deepvalley.place;

import jjhhyb.deepvalley.entityId.PlaceTagId;
import jjhhyb.deepvalley.place.valley.Valley;
import jjhhyb.deepvalley.place.valley.dto.ValleyDetailResponse;
import jjhhyb.deepvalley.place.valley.repository.ValleyRepository;
import jjhhyb.deepvalley.tag.PlaceTagRepository;
import jjhhyb.deepvalley.tag.TagRepository;
import jjhhyb.deepvalley.tag.entity.PlaceTag;
import jjhhyb.deepvalley.tag.entity.Tag;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//For MySQL
public class ValleyIntegrationTests {

    @Autowired
    ValleyRepository valleyRepository;
    @Autowired
    PlaceTagRepository placeTagRepository;
    @Autowired
    TagRepository tagRepository;

    @Autowired
    TestRestTemplate testRestTemplate;

    final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 0);

    @BeforeAll
    void init() {
        Valley[] valleys = {
                Valley.builder().name("21990m, TagA TagB").location(geometryFactory.createPoint(new Coordinate(-106.04614590227612, 38.63804473972625))).avgRating(2.5).build(),
                Valley.builder().name("30700m, Key, TagA").location(geometryFactory.createPoint(new Coordinate(-105.8940008139609, 38.90771378195941))).avgRating(3.0).build(),
                Valley.builder().name("52930m, Key, TagA TagB TagC").location(geometryFactory.createPoint(new Coordinate(-105.21053655004484, 38.79478077778432))).avgRating(4.0).uuid("someId").build(),
                Valley.builder().name("32080m").location(geometryFactory.createPoint(new Coordinate(-105.44350871652753, 38.55071742654455))).avgRating(4.5).build(),
                Valley.builder().name("78850m").location(geometryFactory.createPoint(new Coordinate(-104.93596221047001, 38.404625467569495))).avgRating(5.0).build(),
        };
        //origin -105.78464653173432, 38.64175843889995
        Arrays.stream(valleys).forEach(valleyRepository::save);
        Tag[] tags = {
                new Tag("TagA"), new Tag("TagB"), new Tag("TagC")
        };
        Arrays.stream(tags).forEach(tagRepository::save);

        PlaceTag[] placeTags = {
                PlaceTag.builder().id(new PlaceTagId(valleys[0].getPlaceId(), tags[0].getTagId())).place(valleys[0]).tag(tags[0]).build(),
                PlaceTag.builder().id(new PlaceTagId(valleys[0].getPlaceId(), tags[1].getTagId())).place(valleys[0]).tag(tags[1]).build(),

                PlaceTag.builder().id(new PlaceTagId(valleys[1].getPlaceId(), tags[0].getTagId())).place(valleys[1]).tag(tags[0]).build(),

                PlaceTag.builder().id(new PlaceTagId(valleys[2].getPlaceId(), tags[0].getTagId())).place(valleys[2]).tag(tags[0]).build(),
                PlaceTag.builder().id(new PlaceTagId(valleys[2].getPlaceId(), tags[1].getTagId())).place(valleys[2]).tag(tags[1]).build(),
                PlaceTag.builder().id(new PlaceTagId(valleys[2].getPlaceId(), tags[2].getTagId())).place(valleys[2]).tag(tags[2]).build(),
        };

        Arrays.stream(placeTags).forEach(placeTagRepository::save);
    }

    @Test
    void shouldSearchValleyWithLocation() {
        List responses = testRestTemplate.getForObject("/api/valley?position=-105.78464653173432, 38.64175843889995&radius=33000", List.class);
        assertEquals(3, responses.size());
    }

    @Test
    void shouldSearchValleyWithRating() {
        List responses = testRestTemplate.getForObject("/api/valley?rating=3.5", List.class);
        assertEquals(3, responses.size());
    }

    @Test
    void shouldSearchValleyWithTags() {
        List responses = testRestTemplate.getForObject("/api/valley?tag_names=TagA,TagB", List.class);
        assertEquals(2, responses.size());
    }

    @Test
    void shouldGetValleyDetail() {
        ValleyDetailResponse response = testRestTemplate.getForObject("/api/valley/someId/detail", ValleyDetailResponse.class);

        assertEquals("52930m, Key, TagA TagB TagC", response.getName());
        assertEquals(-105.21053655004484, response.getLongitude());
        assertEquals(38.79478077778432, response.getLatitude());
        assertEquals(4.0, response.getAvgRating());
        assertEquals(List.of("TagA", "TagB", "TagC").toString(), response.getTagNames().toString());
    }

    @Test
    void shouldSearchValleyWithKeyword() {
        List response = testRestTemplate.getForObject("/api/valley?keyword=Key", List.class);

        assertEquals(2, response.size());
    }
}
