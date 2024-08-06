package jjhhyb.deepvalley.banner;

import jjhhyb.deepvalley.banner.dto.response.BannerResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class BannerServiceTest {
    @Test
    @DisplayName("[GET] 메인 배너 조회")
    void getBanner() {
        // Arrange
        BannerService bannerService = new BannerServiceImpl();

        // Act
        BannerResponse bannerResponse = bannerService.getBanner();

        // Assert
        assertThat(bannerResponse).isNotNull();
        assertThat(bannerResponse.getImageUrls()).isNotEmpty();
        assertThat(bannerResponse.getImageUrls()).containsExactly(
                "https://example.com/banner1.jpg",
                "https://example.com/banner2.jpg",
                "https://example.com/banner3.jpg",
                "https://example.com/banner4.jpg",
                "https://example.com/banner5.jpg"
        );
    }
}
