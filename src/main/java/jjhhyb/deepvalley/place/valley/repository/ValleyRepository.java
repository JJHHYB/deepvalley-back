package jjhhyb.deepvalley.place.valley.repository;

import jjhhyb.deepvalley.place.valley.Valley;
import jjhhyb.deepvalley.place.valley.dto.ValleyQueryDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;


public interface ValleyRepository extends JpaRepository<Valley, Long>, CustomizedValleyRepository {

    @Query(value = "select new jjhhyb.deepvalley.place.valley.dto.ValleyQueryDTO(" +
            "v.placeId, v.name, v.uuid, v.thumbnail, v.address, v.contact, v.region, v.content, v.location, v.postCount, v.avgRating, " +
            "v.openingTime, v.closingTime, v.createdDate, v.updatedDate, group_concat(t.name), v.maxDepth, v.avgDepth) " +
            "from Valley v " +
            "left join PlaceTag pt on v.placeId = pt.place.placeId " +
            "left join Tag t on pt.tag.tagId = t.tagId " +
            "where v.uuid = ?1 " +
            "group by v.placeId")
    Optional<ValleyQueryDTO> findByUuid(String uuid);
}
