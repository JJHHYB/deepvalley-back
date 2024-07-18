package jjhhyb.deepvalley.community.dto.response;

import lombok.*;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlaceImageResponse {
    private Long reviewId;
    private List<String> imageUrls;
}
