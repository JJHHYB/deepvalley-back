package jjhhyb.deepvalley.place.valley;

import jakarta.persistence.Entity;
import jjhhyb.deepvalley.place.Place;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@Entity
@NoArgsConstructor
public class Valley extends Place {

    private Integer maxDepth;

    private Integer avgDepth;

}
