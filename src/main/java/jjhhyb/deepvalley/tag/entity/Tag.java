package jjhhyb.deepvalley.tag.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long tagId;

    private String name;

    @OneToMany(mappedBy = "tag", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewTag> reviewTags;

    @OneToMany(mappedBy = "tag", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlaceTag> placeTags;
}
