package jjhhyb.deepvalley.place.valley;

import jakarta.persistence.Entity;
import jjhhyb.deepvalley.place.Place;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor // 기본 생성자 필요
@EqualsAndHashCode(callSuper = true)
@Entity
public class Valley extends Place {

    private Integer maxDepth;

    private Integer avgDepth;

    private Integer postCount;

    private String avgRating;

    private LocalDateTime openingTime;

    private LocalDateTime closingTime;
}
