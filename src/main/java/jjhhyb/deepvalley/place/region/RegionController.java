package jjhhyb.deepvalley.place.region;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jjhhyb.deepvalley.place.region.dto.RegionListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/region")
@RestController
@RequiredArgsConstructor
@Tag(name = "Region", description = "지역 정보 API")
public class RegionController {

    private final RegionService regionService;

    @GetMapping("")
    @Operation(summary = "지역 목록 가져오기", description = "존재하는 지역 목록을 가져옵니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "가져오기 성공")
    })
    public ResponseEntity<RegionListResponse> getRegionList() {
        RegionListResponse response = RegionListResponse.builder().regions(regionService.getRegionNameList()).build();
        return ResponseEntity.ok(response);
    }
}
