package jjhhyb.deepvalley.place.valley;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Valley", description = "계곡 정보 API")
public class ValleyController {

    private final ValleyService valleyService;

    @GetMapping("")
    @Operation(summary = "계곡 검색", description = "여러 조건으로 계곡을 검색합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "계곡 검색 성공")
    })
    public ResponseEntity<List<ValleyResponse>> searchValleys(@RequestParam Optional<String> keyword,
                                                              @RequestParam Optional<String> region,
                                                              @RequestParam Optional<List<Double>> position,
                                                              @RequestParam("tag_names") Optional<List<String>> tagNames,
                                                              @RequestParam(defaultValue = "10000") Long radius,
                                                              @RequestParam(required = false) Optional<Double> rating,
                                                              @RequestParam(defaultValue = "0") Long offset) {
        List<ValleyResponse> valleyResponses = valleyService.searchValleys(keyword, region, position, tagNames, radius, rating, offset);
        return ResponseEntity.ok(valleyResponses);
    }

    @Operation(summary = "계곡 상세 조회", description = "계곡의 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "계곡 조회 성공")
    })
    @GetMapping("/{valleyId}/detail")
    public ResponseEntity<ValleyDetailResponse> getValleyDetail(@PathVariable String valleyId) {
        ValleyDetailResponse valleyDetailResponse = valleyService.getValleyByUuid(valleyId);
        return ResponseEntity.ok(valleyDetailResponse);
    }
}
