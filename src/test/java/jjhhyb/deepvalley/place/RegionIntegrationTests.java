package jjhhyb.deepvalley.place;

import jjhhyb.deepvalley.place.region.Region;
import jjhhyb.deepvalley.place.region.RegionRepository;
import jjhhyb.deepvalley.place.region.dto.RegionListResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RegionIntegrationTests {

    @Autowired
    TestRestTemplate testRestTemplate;

    @Autowired
    RegionRepository regionRepository;

    @Test
    void shouldGetRegionList() {
        regionRepository.save(Region.builder().name("A").build());
        regionRepository.save(Region.builder().name("B").build());

        RegionListResponse response = testRestTemplate.getForObject("/api/region", RegionListResponse.class);
        assertEquals("[A, B]", response.getRegions().toString());
    }
}
