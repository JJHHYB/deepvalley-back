package jjhhyb.deepvalley.place.valley.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.LocalTime;

@Getter
@SuperBuilder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ValleyDetailResponse extends ValleyResponse {

    private final String contact;
    private final String content;
    private final LocalTime openingTime;
    private final LocalTime closingTime;

}
