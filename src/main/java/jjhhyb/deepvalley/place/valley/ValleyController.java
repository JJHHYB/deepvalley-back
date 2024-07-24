package jjhhyb.deepvalley.place.valley;

import jjhhyb.deepvalley.place.valley.dto.ValleyDetailResponse;
import jjhhyb.deepvalley.place.valley.dto.ValleyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequestMapping("/api/valley")
@RestController
@RequiredArgsConstructor
public class ValleyController {

    private final ValleyService valleyService;

    @GetMapping("")
    public ResponseEntity<List<ValleyResponse>> searchValleys(@RequestParam Optional<List<Double>> position,
                                              @RequestParam("tag_names") Optional<List<String>> tagNames,
                                              @RequestParam(defaultValue = "10000") Long radius,
                                              @RequestParam(required = false) Optional<Double> rating,
                                              @RequestParam(defaultValue = "0") Long offset) {
        List<ValleyResponse> valleyResponses = valleyService.searchValleys(position, tagNames, radius, rating, offset);
        return ResponseEntity.ok(valleyResponses);
    }

    @GetMapping("/{valleyId}/detail")
    public ResponseEntity<ValleyDetailResponse> getValleyDetail(@PathVariable String valleyId) {
        ValleyDetailResponse valleyDetailResponse = valleyService.getValleyByUuid(valleyId);
        return ResponseEntity.ok(valleyDetailResponse);
    }
}
