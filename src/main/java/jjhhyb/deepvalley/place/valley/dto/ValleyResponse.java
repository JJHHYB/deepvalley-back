package jjhhyb.deepvalley.place.valley.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@SuperBuilder
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ValleyResponse {

    private final String name;
    private final String valleyId;
    private final String thumbnail;
    private final String address;
    private final String region;
    private final Double latitude;
    private final Double longitude;
    private final Integer maxDepth;
    private final Integer avgDepth;
    private final List<String> tagNames;
    private final Integer postCount;
    private final Double avgRating;
}
