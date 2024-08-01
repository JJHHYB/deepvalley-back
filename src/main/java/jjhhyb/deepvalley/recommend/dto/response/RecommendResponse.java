package jjhhyb.deepvalley.recommend.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jjhhyb.deepvalley.community.entity.Review;
import jjhhyb.deepvalley.place.Place;
import jjhhyb.deepvalley.tag.entity.ReviewTag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RecommendResponse {
    private String placeId;
    private String valleyName;
    private String address;
    private String imageUrl;
    private List<String> tagNames;

    public static RecommendResponse from(Place place, List<Review> reviews) {
        String imageUrl = place.getThumbnail();

        // 글자 수의 총합이 10을 넘지 않도록 태그 이름을 수집
        StringBuilder tagBuilder = new StringBuilder();
        for (Review review : reviews) {
            for (ReviewTag reviewTag : review.getReviewTags()) {
                String tag = reviewTag.getTag().getName();
                if (tagBuilder.length() + tag.length() <= 10) {
                    if (!tagBuilder.isEmpty()) tagBuilder.append(", ");
                    tagBuilder.append(tag);
                }
            }
        }

        List<String> tagNames = List.of(tagBuilder.toString().split(", "));

        return RecommendResponse.builder()
                .placeId(String.valueOf(place.getUuid()))
                .valleyName(place.getName())
                .address(place.getAddress())
                .imageUrl(imageUrl)
                .tagNames(tagNames)
                .build();
    }
}
