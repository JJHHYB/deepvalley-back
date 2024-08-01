package jjhhyb.deepvalley.place.valley.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalTime;
import java.util.List;

@Getter
@SuperBuilder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@NoArgsConstructor
public class ValleyDetailResponse extends ValleyResponse {

    private List<String> tagNames;
    private String tel;
    private String site;
    private Integer zipcode;
    private String content;
    private String openingHours;
    private String extraInfo;

}
