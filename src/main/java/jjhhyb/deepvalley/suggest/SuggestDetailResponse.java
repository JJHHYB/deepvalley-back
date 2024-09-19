package jjhhyb.deepvalley.suggest;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SuggestDetailResponse {
    private String suggestId;
    private String title;
    private String content;
    private String memberId;
    private String placeId;
    private String valleyName;
    private String createdDate;
    private String updatedDate;
    private List<String> imageUrls;

    public static SuggestDetailResponse from(Suggest suggest) {
        return SuggestDetailResponse.builder()
                .suggestId(String.valueOf(suggest.getUuid()))
                .title(suggest.getTitle())
                .content(suggest.getContent())
                .memberId(String.valueOf(suggest.getMember().getLoginEmail()))
                .placeId(String.valueOf(suggest.getPlace().getUuid()))
                .valleyName(suggest.getPlace().getName())
                .createdDate(String.valueOf(suggest.getCreatedDate()))
                .updatedDate(String.valueOf(suggest.getUpdatedDate()))
                .imageUrls((suggest.getSuggestImages() != null ? suggest.getSuggestImages().stream()
                        .map(reviewImage -> reviewImage.getImage().getImageUrl())
                        .collect(Collectors.toList()) : Collections.emptyList()))
                .build();
    }
}
