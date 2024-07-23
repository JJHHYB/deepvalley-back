package jjhhyb.deepvalley.place.valley.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jjhhyb.deepvalley.place.PlaceMapper;
import jjhhyb.deepvalley.place.PlaceUtil;
import jjhhyb.deepvalley.place.valley.Valley;
import jjhhyb.deepvalley.place.valley.dto.ValleyResponse;

import java.util.*;

public class CustomizedValleyRepositoryImpl implements CustomizedValleyRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<ValleyResponse> searchValleys(Optional<List<Double>> position, Optional<List<String>> tagNames, Long radius, Optional<Double> rating, Long offset) {
        StringBuilder queryString = new StringBuilder(
                "select v " +
                "from Valley v ");
        StringJoiner joiner = new StringJoiner(" AND ");
        Map<String, Object> parameters = new HashMap<>();

        rating.ifPresent(value -> {
            joiner.add("v.avgRating>=:rating");
            parameters.put("rating", value);
        });

        tagNames.ifPresent(value -> {
            queryString.append("join place_tag pt on v.place_id = pt.place_id " +
                    "join tag t on pt.tag_id = t.tag_id ");
            joiner.add("t.name in (:tags)");
            parameters.put("tags", String.join(", ", value));
        });

        position.ifPresent(value -> {
            if(value.size() >= 2) {
                double[] boundingBox = PlaceUtil.getBoundingBoxVertexByCircle(value.get(0), value.get(1), radius);
                joiner.add("ST_Contains(ST_MakeEnvelope(ST_GeomFromText('POINT(%s %s)'), ST_GeomFromText('POINT(%s %s)')), v.location) AND (ST_Distance_Sphere(ST_GeomFromText('POINT(%s %s)'), v.location)) <= :radius"
                        .formatted(boundingBox[0], boundingBox[1], boundingBox[2], boundingBox[3], value.get(0), value.get(1)));
                parameters.put("radius", radius);
            }
        });

        if(joiner.length() > 0) {
            queryString.append("WHERE ");
            queryString.append(joiner.toString());
        }

        tagNames.ifPresent(value -> {
            queryString.append("group by v.place_id " +
                    "having count(t.name) >= :tagCount");
            parameters.put("tagCount", value.size());
        });

        TypedQuery<Valley> query = em.createQuery(queryString.toString(), Valley.class);

        parameters.forEach(query::setParameter);

        if(position.isEmpty()) {
            query.setMaxResults(10);
            query.setFirstResult(offset.intValue());
        }

        return PlaceMapper.INSTANCE.valleysToValleyResponses(query.getResultList());
    }
}
