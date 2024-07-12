package jjhhyb.deepvalley.place.valley;

import jakarta.persistence.Entity;
import jjhhyb.deepvalley.place.Place;

import java.time.LocalDateTime;

@Entity
public class Valley extends Place {

    private Integer maxDepth;

    private Integer avgDepth;

    private Integer postCount;

    private String avgRating;

    private LocalDateTime openingTime;

    private LocalDateTime closingTime;
}
