package jjhhyb.deepvalley.suggest;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@EqualsAndHashCode
@Embeddable
@Getter
public class SuggestImageId implements Serializable {
    @Column(name = "SUGGEST_ID")
    private Long suggestId;

    @Column(name = "IMAGE_ID")
    private Long imageId;

    public SuggestImageId(Long suggestId, Long imageId) {
        this.suggestId = suggestId;
        this.imageId = imageId;
    }
}
