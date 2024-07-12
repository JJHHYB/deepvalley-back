package jjhhyb.deepvalley.entityId;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public abstract class BaseId<T extends BaseId<T>> implements Serializable {

    private Long primaryId;
    private Long secondaryId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseId<?> that = (BaseId<?>) o;
        return Objects.equals(primaryId, that.primaryId) && Objects.equals(secondaryId, that.secondaryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(primaryId, secondaryId);
    }
}
