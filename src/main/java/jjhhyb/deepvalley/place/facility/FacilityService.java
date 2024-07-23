package jjhhyb.deepvalley.place.facility;

import jjhhyb.deepvalley.place.facility.dto.FacilityResponse;
import jjhhyb.deepvalley.place.facility.repository.FacilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FacilityService {

    private final FacilityRepository facilityRepository;

    public List<FacilityResponse> searchFacilities(Optional<List<Double>> position,
                                                   Optional<List<String>> tagNames,
                                                   Long radius) {
        return facilityRepository.searchFacilities(position, tagNames, radius);
    }
}
