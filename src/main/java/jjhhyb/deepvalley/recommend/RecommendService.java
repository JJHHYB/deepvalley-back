package jjhhyb.deepvalley.recommend;

import jjhhyb.deepvalley.community.entity.Review;
import jjhhyb.deepvalley.community.repository.ReviewRepository;
import jjhhyb.deepvalley.place.Place;
import jjhhyb.deepvalley.recommend.dto.response.RecommendResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RecommendService {
    private final ReviewRepository reviewRepository;

    public List<RecommendResponse> getRecommendPlace() {
        LocalDate oneWeekAgo = LocalDate.now().minusWeeks(1);

        // 최근 1주일간의 모든 리뷰를 가져옴
        List<Review> recentReviews = reviewRepository.findByVisitedDateAfter(oneWeekAgo);

        // 계곡별로 리뷰를 그룹화하고, 평균 평점을 계산
        Map<Place, Double> placeAvgRatingMap = recentReviews.stream()
                .collect(Collectors.groupingBy(Review::getPlace,
                        Collectors.averagingDouble(review -> review.getRating().getRating())));

        // 평점이 높은 순으로 정렬하고 상위 5개 계곡을 선택
        return placeAvgRatingMap.entrySet().stream()
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                .limit(5)
                .map(entry -> {
                    Place place = entry.getKey();
                    List<Review> reviews = recentReviews.stream()
                            .filter(review -> review.getPlace().equals(place))
                            .collect(Collectors.toList());
                    return RecommendResponse.from(place, reviews);
                })
                .collect(Collectors.toList());
    }
}
