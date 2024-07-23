package jjhhyb.deepvalley.place.valley;

import jjhhyb.deepvalley.place.exception.PlaceNotFoundException;
import jjhhyb.deepvalley.place.valley.dto.ValleyResponse;
import jjhhyb.deepvalley.place.valley.repository.ValleyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ValleyService {

    private final ValleyRepository valleyRepository;

    public Valley getValleyByUuid(String uuid) {
        Optional<Valley> valleyOptional = valleyRepository.findByUuid(uuid);
        return valleyOptional.orElseThrow(() -> new PlaceNotFoundException("존재하지 않는 계곡 ID 입니다."));
    }

    public List<ValleyResponse> searchValleys(Optional<List<Double>> position,
                                              Optional<List<String>> tagNames,
                                              Long radius,
                                              Optional<Double> rating,
                                              Long offset) {
        return valleyRepository.searchValleys(position, tagNames, radius, rating, offset);
    }
}
