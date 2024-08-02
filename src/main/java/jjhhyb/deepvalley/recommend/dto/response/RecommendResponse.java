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
import java.util.stream.Collectors;

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

        // 태그 이름을 중복을 포함하여 리스트로 생성
        List<String> tagNames = reviews.stream()
                .flatMap(review -> review.getReviewTags().stream())
                .map(reviewTag -> reviewTag.getTag().getName())
                .collect(Collectors.toList());

        return RecommendResponse.builder()
                .placeId(String.valueOf(place.getUuid()))
                .valleyName(place.getName())
                .address(place.getAddress())
                .imageUrl(place.getThumbnail())
                .tagNames(tagNames)
                .build();
    }
}
