package jjhhyb.deepvalley.community.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class ReviewsResponse {
    private List<ReviewDetailResponse> reviews;
}
