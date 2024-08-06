package jjhhyb.deepvalley.banner;

import jjhhyb.deepvalley.banner.dto.response.BannerResponse;
import jjhhyb.deepvalley.image.S3Service;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@SpringBootTest
public class BannerServiceTest {
    @Autowired
    private BannerService bannerService;

    @MockBean
    private S3Service s3Service;
    @Test
    @DisplayName("[GET] 메인 배너 조회")
    void getBanner() {
        // Arrange
        List<String> mockImageUrls = List.of(
                "https://deep-valley-image.s3.ap-northeast-2.amazonaws.com/banner-images/Group_37.png",
                "https://deep-valley-image.s3.ap-northeast-2.amazonaws.com/banner-images/Group_41.png",
                "https://deep-valley-image.s3.ap-northeast-2.amazonaws.com/banner-images/Group_43.png",
                "https://deep-valley-image.s3.ap-northeast-2.amazonaws.com/banner-images/Group_44.png",
                "https://deep-valley-image.s3.ap-northeast-2.amazonaws.com/banner-images/Group_46.png"
        );

        given(s3Service.listFilesInFolder("banner-images")).willReturn(mockImageUrls);

        // Act
        BannerResponse bannerResponse = bannerService.getBanner();

        // Assert
        assertThat(bannerResponse).isNotNull();
        assertThat(bannerResponse.getImageUrls()).isNotEmpty();
        assertThat(bannerResponse.getImageUrls()).containsExactlyElementsOf(mockImageUrls);
    }
}
