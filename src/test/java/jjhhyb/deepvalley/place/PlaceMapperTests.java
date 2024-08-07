package jjhhyb.deepvalley.place;

import jjhhyb.deepvalley.place.facility.Facility;
import jjhhyb.deepvalley.place.facility.dto.FacilityResponse;
import jjhhyb.deepvalley.place.valley.Valley;
import jjhhyb.deepvalley.place.valley.dto.ValleyDetailResponse;
import jjhhyb.deepvalley.place.valley.dto.ValleyQueryDTO;
import jjhhyb.deepvalley.place.valley.dto.ValleyResponse;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PlaceMapperTests {

    final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 0);

    @Test
    void shouldMapValleyToValleyDetailResponse() {
        ValleyQueryDTO valleyQueryDTO = ValleyQueryDTO.builder()
                .name("valleyA")
                .uuid("sampleId")
                .thumbnail("someURL")
                .address("someAddress")
                .zipcode("111")
                .tel("010")
                .site("a.com")
                .openingHours("10:00~19:00")
                .extraInfo("{a: a}")
                .region("someRegion")
                .content("someContent")
                .location(geometryFactory.createPoint(new Coordinate(1.5, 1.5)))
                .postCount(10)
                .avgRating(2.5)
                .maxDepth(5)
                .avgDepth(2)
                .tagNames("A,B")
                .build();

        ValleyDetailResponse response = PlaceMapper.INSTANCE.valleyQueryDTOToValleyDetailResponse(valleyQueryDTO);

        assertNotNull(response);
        assertEquals("valleyA", response.getName());
        assertEquals("sampleId", response.getValleyId());
        assertEquals("someURL", response.getThumbnail());
        assertEquals("someAddress", response.getAddress());
        assertEquals("someRegion", response.getRegion());
        assertEquals("someContent", response.getContent());
        assertEquals(1.5, response.getLatitude());
        assertEquals(1.5, response.getLongitude());
        assertEquals(10, response.getPostCount());
        assertEquals(2.5, response.getAvgRating());
        assertEquals(5, response.getMaxDepth());
        assertEquals(2, response.getAvgDepth());
        assertEquals("[A, B]", response.getTagNames().toString());
        assertEquals("111", response.getZipcode());
        assertEquals("010", response.getTel());
        assertEquals("a.com", response.getSite());
        assertEquals("10:00~19:00", response.getOpeningHours());
        assertEquals("{a: a}", response.getExtraInfo());
    }

    @Test
    void shouldMapValleysToValleyResponses() {
        ValleyQueryDTO valley1 = ValleyQueryDTO.builder()
                .name("valleyA")
                .uuid("sampleId")
                .thumbnail("someURL")
                .address("someAddress")
                .region("someRegion")
                .content("someContent")
                .location(geometryFactory.createPoint(new Coordinate(1.5, 1.5)))
                .postCount(10)
                .avgRating(2.5)
                .maxDepth(5)
                .avgDepth(2)
                .build();
        ValleyQueryDTO valley2 = ValleyQueryDTO.builder()
                .name("valleyB")
                .build();

        List<ValleyQueryDTO> valleys = List.of(valley1, valley2);
        List<ValleyResponse> valleyResponses = PlaceMapper.INSTANCE.valleyQueryDTOsToValleyResponses(valleys);

        ValleyResponse response1 = valleyResponses.get(0);
        ValleyResponse response2 = valleyResponses.get(1);

        assertNotNull(response1);
        assertEquals("valleyA", response1.getName());
        assertEquals("sampleId", response1.getValleyId());
        assertEquals("someURL", response1.getThumbnail());
        assertEquals("someAddress", response1.getAddress());
        assertEquals("someRegion", response1.getRegion());
        assertEquals(1.5, response1.getLatitude());
        assertEquals(1.5, response1.getLongitude());
        assertEquals(10, response1.getPostCount());
        assertEquals(2.5, response1.getAvgRating());
        assertEquals(5, response1.getMaxDepth());
        assertEquals(2, response1.getAvgDepth());

        assertEquals("valleyB", response2.getName());
    }

    @Test
    void shouldMapFacilityToFacilityResponse() {
        Facility facility = Facility.builder()
                .name("facilityA")
                .uuid("sampleId")
                .thumbnail("someURL")
                .address("someAddress")
                .region("someRegion")
                .location(geometryFactory.createPoint(new Coordinate(1.5, 1.5)))
                .build();

        FacilityResponse response = PlaceMapper.INSTANCE.facilityToFacilityResponse(facility);

        assertNotNull(response);
        assertEquals("facilityA", response.getName());
        assertEquals("sampleId", response.getFacilityId());
        assertEquals("someURL", response.getThumbnail());
        assertEquals("someAddress", response.getAddress());
        assertEquals("someRegion", response.getRegion());
        assertEquals(1.5, response.getLatitude());
        assertEquals(1.5, response.getLongitude());
    }

    @Test
    void shouldMapFacilitiesToFacilityResponses() {
        Facility facility1 = Facility.builder()
                .name("facilityA")
                .uuid("sampleId")
                .thumbnail("someURL")
                .address("someAddress")
                .region("someRegion")
                .location(geometryFactory.createPoint(new Coordinate(1.5, 1.5)))
                .build();

        Facility facility2 = Facility.builder()
                .name("facilityB")
                .build();

        List<Facility> facilities = List.of(facility1, facility2);
        List<FacilityResponse> responses = PlaceMapper.INSTANCE.facilitiesToFacilityResponses(facilities);

        assertNotNull(responses);
        FacilityResponse response1 = responses.get(0);
        FacilityResponse response2 = responses.get(1);


        assertEquals("facilityA", response1.getName());
        assertEquals("sampleId", response1.getFacilityId());
        assertEquals("someURL", response1.getThumbnail());
        assertEquals("someAddress", response1.getAddress());
        assertEquals("someRegion", response1.getRegion());
        assertEquals(1.5, response1.getLatitude());
        assertEquals(1.5, response1.getLongitude());

        assertEquals("facilityB", response2.getName());
    }
}
