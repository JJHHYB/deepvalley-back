package jjhhyb.deepvalley.place;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@DiscriminatorColumn
@Inheritance(strategy = InheritanceType.JOINED)
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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

}
