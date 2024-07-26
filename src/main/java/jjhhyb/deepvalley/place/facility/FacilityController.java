package jjhhyb.deepvalley.place.facility;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jjhhyb.deepvalley.place.facility.dto.FacilityResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/facility")
public class FacilityController {

    private final FacilityService facilityService;

    @GetMapping("")
    @Operation(summary = "시설 검색", description = "시설을 검색합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "시설 검색 성공")
    })
    public ResponseEntity<List<FacilityResponse>> searchFacilities(@RequestParam Optional<List<Double>> position,
                                                                 @RequestParam(defaultValue = "10000") Long radius) {
        position.ifPresentOrElse(value -> {
            if(value.size() < 2) {
                throw new IllegalArgumentException("위치 값이 잘못되었습니다.");
            }
        },() -> {
            throw new IllegalArgumentException("위치 값은 필수입니다.");
        });

        List<FacilityResponse> facilityResponses = facilityService.searchFacilities(position, radius);
        return ResponseEntity.ok(facilityResponses);
    }

}
