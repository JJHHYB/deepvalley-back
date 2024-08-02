package jjhhyb.deepvalley.recommend;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jjhhyb.deepvalley.community.dto.response.ReviewsResponse;
import jjhhyb.deepvalley.recommend.dto.response.RecommendResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Recommend", description = "추천 계곡 API")
public class RecommendController {

    private final RecommendService recommendService;

    @GetMapping("/api/review/recommend")
    @Operation(summary = "추천 계곡 조회", description = "추천 계곡을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "추천 계곡 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RecommendResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    public List<RecommendResponse> getSpecificMemberReviews() {
        return recommendService.getRecommendPlace();
    }
}
