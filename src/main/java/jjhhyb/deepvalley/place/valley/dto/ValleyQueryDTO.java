package jjhhyb.deepvalley.place.valley.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValleyQueryDTO {

    private Long placeId;

    private String name;

    private String uuid;

    private String thumbnail;

    private String address;

    private String contact;

    private String region;

    private String content;

    private Point location;

    private Integer postCount;

    private Double avgRating;

    private LocalTime openingTime;

    private LocalTime closingTime;

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;

    private String tagNames;

    private Integer maxDepth;

    private Integer avgDepth;

}
