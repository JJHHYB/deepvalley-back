package jjhhyb.deepvalley.place.valley.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
    @Builder.Default
    private String thumbnail = "https://deep-valley-image.s3.ap-northeast-2.amazonaws.com/default-image/valley_thumbnail_default.png";
    private String address;
    private String region;
    private Double latitude;
    private Double longitude;
    private Integer maxDepth;
    private Integer avgDepth;
    private Integer postCount;
    private Double avgRating;
}
