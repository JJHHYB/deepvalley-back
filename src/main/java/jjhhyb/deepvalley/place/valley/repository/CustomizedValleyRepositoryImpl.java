package jjhhyb.deepvalley.place.valley.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jjhhyb.deepvalley.place.PlaceMapper;
import jjhhyb.deepvalley.place.PlaceUtil;
import jjhhyb.deepvalley.place.valley.Valley;
import jjhhyb.deepvalley.place.valley.dto.ValleyQueryDTO;
import jjhhyb.deepvalley.place.valley.dto.ValleyResponse;

import java.util.*;

public class CustomizedValleyRepositoryImpl implements CustomizedValleyRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<ValleyResponse> searchValleys(Optional<List<Double>> position, Optional<List<String>> tagNames, Long radius, Optional<Double> rating, Long offset) {
        String selectString =
                "select new jjhhyb.deepvalley.place.valley.dto.ValleyQueryDTO(" +
                        "v.placeId, v.name, v.uuid, v.thumbnail, v.address, v.contact, v.region, v.content, v.location, v.postCount, v.avgRating, " +
                        "v.openingTime, v.closingTime, v.createdDate, v.updatedDate, null, v.maxDepth, v.avgDepth) " +
                "from Valley v ";
        StringBuilder queryString = new StringBuilder(selectString);
//        if(tagNames.isPresent()) {
//            queryString.append(selectString.formatted("group_concat(t.name)"));
//        } else {
//            queryString.append(selectString.formatted(""));
//        }
        StringJoiner joiner = new StringJoiner(" AND ");
        Map<String, Object> parameters = new HashMap<>();

        rating.ifPresent(value -> {
            joiner.add("v.avgRating>=:rating");
            parameters.put("rating", value);
        });

        tagNames.ifPresent(value -> {
            queryString.append("join PlaceTag pt on v.placeId = pt.place.placeId " +
                    "join Tag t on pt.tag.tagId = t.tagId ");
            joiner.add("t.name in ('%s')".formatted(String.join("', '", value)));
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
            queryString.append(joiner.toString()).append(" ");
        }

        tagNames.ifPresent(value -> {
            queryString.append("group by v.placeId " +
                    "having count(t.name) >= :tagCount");
            parameters.put("tagCount", value.size());
        });

        TypedQuery<ValleyQueryDTO> query = em.createQuery(queryString.toString(), ValleyQueryDTO.class);

        parameters.forEach(query::setParameter);

        if(position.isEmpty()) {
            query.setMaxResults(10);
            query.setFirstResult(offset.intValue());
        }
        return PlaceMapper.INSTANCE.valleyQueryDTOsToValleyResponses(query.getResultList());
    }
}