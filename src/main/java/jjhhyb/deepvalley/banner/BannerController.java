package jjhhyb.deepvalley.banner;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jjhhyb.deepvalley.banner.dto.response.BannerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Banner", description = "배너 관리 API")
public class BannerController {
    private final BannerService bannerService;
    @GetMapping("/api/banner")
    @Operation(summary = "배너 조회", description = "main에 표시할 배너를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "배너 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BannerResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    public BannerResponse getBanner() {
        return bannerService.getBanner();
    }
}
