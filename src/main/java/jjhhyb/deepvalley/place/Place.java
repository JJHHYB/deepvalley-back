package jjhhyb.deepvalley.place;

import jakarta.persistence.*;
import jjhhyb.deepvalley.tag.entity.PlaceTag;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@DiscriminatorColumn
@Inheritance(strategy = InheritanceType.JOINED)
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long placeId;

    private String name;

    private String uuid;

    private String address;

    private String region;

    private String content;

    private BigDecimal latitude;

    private BigDecimal longitude;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime updatedDate;

//    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<PlaceTag> placeTags;
}
