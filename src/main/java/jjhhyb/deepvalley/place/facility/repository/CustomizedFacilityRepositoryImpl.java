package jjhhyb.deepvalley.place.facility.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jjhhyb.deepvalley.place.PlaceMapper;
import jjhhyb.deepvalley.place.PlaceUtil;
import jjhhyb.deepvalley.place.facility.Facility;
import jjhhyb.deepvalley.place.facility.dto.FacilityResponse;

import java.util.*;

public class CustomizedFacilityRepositoryImpl implements CustomizedFacilityRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<FacilityResponse> searchFacilities(Optional<List<Double>> position, Optional<List<String>> tagNames, Long radius) {
        StringBuilder queryString = new StringBuilder(
                "select f " +
                        "from Facility f ");
        StringJoiner joiner = new StringJoiner(" AND ");
        Map<String, Object> parameters = new HashMap<>();

        tagNames.ifPresent(value -> {
            queryString.append("join place_tag pt on f.place_id = pt.place_id " +
                    "join tag t on pt.tag_id = t.tag_id ");
            joiner.add("t.name in (:tags)");
            parameters.put("tags", String.join(", ", value));
        });

        position.ifPresent(value -> {
                double[] boundingBox = PlaceUtil.getBoundingBoxVertexByCircle(value.get(0), value.get(1), radius);
                joiner.add("ST_Contains(ST_MakeEnvelope(ST_GeomFromText('POINT(%s %s)'), ST_GeomFromText('POINT(%s %s)')), f.location) AND (ST_Distance_Sphere(ST_GeomFromText('POINT(%s %s)'), f.location)) <= :radius"
                        .formatted(boundingBox[0], boundingBox[1], boundingBox[2], boundingBox[3], value.get(0), value.get(1)));
                parameters.put("radius", radius);
        });

        if(joiner.length() > 0) {
            queryString.append("WHERE ");
            queryString.append(joiner.toString());
        }

        tagNames.ifPresent(value -> {
            queryString.append("group by f.place_id " +
                    "having count(t.name) >= :tagCount");
            parameters.put("tagCount", value.size());
        });

        TypedQuery<Facility> query = em.createQuery(queryString.toString(), Facility.class);

        parameters.forEach(query::setParameter);

        return PlaceMapper.INSTANCE.facilitiesToFacilityResponses(query.getResultList());
    }
}
