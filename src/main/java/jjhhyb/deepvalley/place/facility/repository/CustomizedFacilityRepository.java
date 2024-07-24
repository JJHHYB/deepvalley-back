package jjhhyb.deepvalley.place.facility.repository;

import jjhhyb.deepvalley.place.facility.dto.FacilityResponse;

import java.util.List;
import java.util.Optional;

public interface CustomizedFacilityRepository {
    List<FacilityResponse> searchFacilities(Optional<List<Double>> position, Long radius);
}
