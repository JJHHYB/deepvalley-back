package jjhhyb.deepvalley.place;

import jjhhyb.deepvalley.place.facility.Facility;
import jjhhyb.deepvalley.place.facility.dto.FacilityResponse;
import jjhhyb.deepvalley.place.valley.Valley;
import jjhhyb.deepvalley.place.valley.dto.ValleyDetailResponse;
import jjhhyb.deepvalley.place.valley.dto.ValleyResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface PlaceMapper {
    PlaceMapper INSTANCE = Mappers.getMapper(PlaceMapper.class);

    @Mapping(source = "uuid", target = "valleyId")
    @Mapping(source = "location.x", target = "longitude")
    @Mapping(source = "location.y", target = "latitude")
    ValleyDetailResponse valleyToValleyDetailResponse(Valley valley);

    List<ValleyResponse> valleysToValleyResponses(List<Valley> valleys);

    @Mapping(source = "uuid", target = "facilityId")
    @Mapping(source = "location.x", target = "longitude")
    @Mapping(source = "location.y", target = "latitude")
    FacilityResponse facilityToFacilityResponse(Facility facility);

    List<FacilityResponse> facilitiesToFacilityResponses(List<Facility> facilities);
}
