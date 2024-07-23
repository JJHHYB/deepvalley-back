package jjhhyb.deepvalley.place.facility.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class FacilityResponse {
    private final String name;
    private final String facilityId;
    private final String thumbnail;
    private final String address;
    private final String region;
    private final Double latitude;
    private final Double longitude;
    private final List<String> tagNames;
}
