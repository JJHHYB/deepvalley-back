package jjhhyb.deepvalley.place;

import jjhhyb.deepvalley.place.valley.Valley;
import jjhhyb.deepvalley.place.valley.repository.ValleyRepository;
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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//For MySQL
public class ValleyIntegrationTests {

    @Autowired
    ValleyRepository valleyRepository;

    @Autowired
    TestRestTemplate testRestTemplate;

    final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 0);

    @BeforeAll
    void init() {
        Valley[] valleys = {
                Valley.builder().name("21990m").location(geometryFactory.createPoint(new Coordinate(-106.04614590227612, 38.63804473972625))).avgRating(2.5).build(),
                Valley.builder().name("30700m").location(geometryFactory.createPoint(new Coordinate(-105.8940008139609, 38.90771378195941))).avgRating(3.0).build(),
                Valley.builder().name("52930m").location(geometryFactory.createPoint(new Coordinate(-105.21053655004484, 38.79478077778432))).avgRating(4.0).build(),
                Valley.builder().name("32080m").location(geometryFactory.createPoint(new Coordinate(-105.44350871652753, 38.55071742654455))).avgRating(4.5).build(),
                Valley.builder().name("78850m").location(geometryFactory.createPoint(new Coordinate(-104.93596221047001, 38.404625467569495))).avgRating(5.0).build(),};
        //origin -105.78464653173432, 38.64175843889995
        Arrays.stream(valleys).forEach(valleyRepository::save);
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
}
