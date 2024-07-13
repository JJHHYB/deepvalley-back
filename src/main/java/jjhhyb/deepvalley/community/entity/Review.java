package jjhhyb.deepvalley.community.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.List;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Data
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    private String uuid;
    private String title;
    private ReviewRating rating;
    private String content;
    private LocalDateTime visitedDate;
    private ReviewPrivacy privacy;

    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "valley_id")
    private Long valleyId;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime updatedDate;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewImage> reviewImages;
}
