package jjhhyb.deepvalley.banner;

import jjhhyb.deepvalley.banner.dto.response.BannerResponse;
import jjhhyb.deepvalley.image.S3Service;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@SpringBootTest
public class BannerServiceTest {
    @Autowired
    private BannerService bannerService;

    @MockBean
    private S3Service s3Service;
    @Test
    @DisplayName("[GET] 메인 배너 조회")
    void getBanner() {
        // given
        List<String> mockImageUrls = Arrays.asList("url1", "url2", "url3");
        when(s3Service.listFilesInFolder("banner-images")).thenReturn(mockImageUrls);

        // when
        BannerResponse response = bannerService.getBanner();

        // then
        assertEquals(mockImageUrls, response.getImageUrls());
    }
}
