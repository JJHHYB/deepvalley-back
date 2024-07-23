package jjhhyb.deepvalley.place;

import jjhhyb.deepvalley.place.facility.Facility;
import jjhhyb.deepvalley.place.facility.repository.FacilityRepository;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//For MySQL
public class FacilityIntegrationTests {

    @Autowired
    FacilityRepository facilityRepository;

    @Autowired
    TestRestTemplate testRestTemplate;

    final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 0);

    @BeforeAll
    void init() {
        Facility[] facilities = {
                Facility.builder().name("21990m").location(geometryFactory.createPoint(new Coordinate(-106.04614590227612, 38.63804473972625))).build(),
                Facility.builder().name("30700m").location(geometryFactory.createPoint(new Coordinate(-105.8940008139609, 38.90771378195941))).build(),
                Facility.builder().name("52930m").location(geometryFactory.createPoint(new Coordinate(-105.21053655004484, 38.79478077778432))).build(),
                Facility.builder().name("32080m").location(geometryFactory.createPoint(new Coordinate(-105.44350871652753, 38.55071742654455))).build(),
                Facility.builder().name("78850m").location(geometryFactory.createPoint(new Coordinate(-104.93596221047001, 38.404625467569495))).build(),};
        //origin -105.78464653173432, 38.64175843889995
        Arrays.stream(facilities).forEach(facilityRepository::save);
    }

    @Test
    void shouldSearchFacilityWithLocation() {
        List responses = testRestTemplate.getForObject("/api/facility?position=-105.78464653173432, 38.64175843889995&radius=33000", List.class);
        assertEquals(3, responses.size());
    }

}
