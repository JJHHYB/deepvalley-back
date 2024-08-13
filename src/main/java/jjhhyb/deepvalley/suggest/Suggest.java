package jjhhyb.deepvalley.suggest;

import jakarta.persistence.*;
import jjhhyb.deepvalley.place.Place;
import jjhhyb.deepvalley.user.entity.Member;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Builder
public class Suggest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long suggestId;
    private String uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    private Place place;

    @OneToMany(mappedBy = "suggest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SuggestImage> suggestImages = new ArrayList<>();

    private String title;
    private String content;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime updatedDate;

    private LocalDateTime visitedDate;
}
