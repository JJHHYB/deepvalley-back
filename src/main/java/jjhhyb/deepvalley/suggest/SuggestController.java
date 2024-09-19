package jjhhyb.deepvalley.suggest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/suggest")
public class SuggestController {
    @Autowired
    private SuggestService suggestService;

    @PostMapping
    public SuggestDetailResponse createSuggest(
            @RequestPart("suggestPostRequest") SuggestPostRequest suggestPostRequest,
            @RequestPart(value = "imageUrls", required = false) List<MultipartFile> imageFiles,
            Authentication auth) {

        return suggestService.createSuggest(suggestPostRequest, imageFiles, auth.getName());
    }

// 정보변경제안 특성상 수정, 삭제 기능이 필요하지 않음
//    @GetMapping
//    public SuggestsResponse getAllSuggests() {
//        return suggestService.getSuggests();
//    }
//
//    @GetMapping("/{suggest-id}")
//    public SuggestDetailResponse getSuggestDetail(@PathVariable("suggest-id") String suggestId) {
//        return suggestService.getSuggestDetail(suggestId);
//    }
//
//    @PutMapping("/{suggest-id}")
//    public SuggestDetailResponse updateSuggest(
//            @PathVariable("suggest-id") String suggestId,
//            @RequestPart("suggestPostRequest") SuggestPostRequest suggestPostRequest,
//            @RequestPart(value = "imageUrls", required = false) List<MultipartFile> imageFiles,
//            Authentication auth) {
//
//        return suggestService.updateSuggest(suggestId, suggestPostRequest, imageFiles, auth.getName());
//    }
//
//    @DeleteMapping("/{suggest-id}")
//    public ResponseEntity<Void> deleteSuggest(@PathVariable("suggest-id") String suggestId, Authentication auth) {
//        suggestService.deleteSuggest(suggestId, auth.getName());
//        return ResponseEntity.noContent().build();
//    }
}