package jjhhyb.deepvalley.suggest;

import jakarta.persistence.*;
import jjhhyb.deepvalley.image.Image;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Builder
public class SuggestImage {

    @EmbeddedId
    private SuggestImageId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("suggestId")
    @JoinColumn(name = "suggest_id")
    private Suggest suggest;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("imageId")
    @JoinColumn(name = "image_id")
    private Image image;
}
