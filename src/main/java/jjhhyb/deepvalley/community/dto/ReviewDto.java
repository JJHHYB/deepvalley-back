package jjhhyb.deepvalley.community.dto;

import jjhhyb.deepvalley.community.entity.ReviewPrivacy;
import jjhhyb.deepvalley.community.entity.ReviewRate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {
    private Long reviewId;
    private String uuid;
    private String title;
    private ReviewRate rating;
    private String content;
    private LocalDateTime visitedDate;
    private ReviewPrivacy privacy;
    private Long memberId;
    private Long placeId;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}

