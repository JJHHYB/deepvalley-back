package jjhhyb.deepvalley.place;

import jjhhyb.deepvalley.place.facility.Facility;
import jjhhyb.deepvalley.place.facility.dto.FacilityResponse;
import jjhhyb.deepvalley.place.valley.Valley;
import jjhhyb.deepvalley.place.valley.dto.ValleyDetailResponse;
import jjhhyb.deepvalley.place.valley.dto.ValleyQueryDTO;
import jjhhyb.deepvalley.place.valley.dto.ValleyResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Arrays;
import java.util.List;

@Mapper
public interface PlaceMapper {
    PlaceMapper INSTANCE = Mappers.getMapper(PlaceMapper.class);

    @Mapping(source = "uuid", target = "valleyId")
    @Mapping(source = "location.x", target = "longitude")
    @Mapping(source = "location.y", target = "latitude")
    ValleyDetailResponse valleyQueryDTOToValleyDetailResponse(ValleyQueryDTO valley);

    default List<String> stringToStringList(String string) {
        if(string == null) {
            return null;
        }
        return Arrays.asList(string.split(","));
    }

    List<ValleyResponse> valleyQueryDTOsToValleyResponses(List<ValleyQueryDTO> valleys);

    @Mapping(source = "uuid", target = "facilityId")
    @Mapping(source = "location.x", target = "longitude")
    @Mapping(source = "location.y", target = "latitude")
    FacilityResponse facilityToFacilityResponse(Facility facility);

    List<FacilityResponse> facilitiesToFacilityResponses(List<Facility> facilities);
}
