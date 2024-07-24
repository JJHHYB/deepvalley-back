package jjhhyb.deepvalley.place.valley.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Getter
@SuperBuilder
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@NoArgsConstructor
public class ValleyResponse {

    private String name;
    private String valleyId;
    private String thumbnail;
    private String address;
    private String region;
    private Double latitude;
    private Double longitude;
    private Integer maxDepth;
    private Integer avgDepth;
    private Integer postCount;
    private Double avgRating;
}
