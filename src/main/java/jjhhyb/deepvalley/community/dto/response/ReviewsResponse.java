package jjhhyb.deepvalley.community.dto.response;

import jjhhyb.deepvalley.community.entity.Review;
import lombok.Data;

import java.util.List;

@Data
public class ReviewsResponse {
    private List<Review> reviews;
}
