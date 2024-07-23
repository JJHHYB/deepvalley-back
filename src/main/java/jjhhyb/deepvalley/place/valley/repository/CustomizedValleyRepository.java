package jjhhyb.deepvalley.place.valley.repository;

import jjhhyb.deepvalley.place.valley.dto.ValleyResponse;

import java.util.List;
import java.util.Optional;

public interface CustomizedValleyRepository {
    List<ValleyResponse> searchValleys(Optional<List<Double>> position, Optional<List<String>> tagNames, Long radius, Optional<Double> rating, Long offset);
}
