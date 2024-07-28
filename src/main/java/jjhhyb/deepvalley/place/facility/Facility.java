package jjhhyb.deepvalley.place.facility;

import jakarta.persistence.Entity;
import jjhhyb.deepvalley.place.Place;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@Entity
@NoArgsConstructor
public class Facility extends Place {

    private String type;
}
