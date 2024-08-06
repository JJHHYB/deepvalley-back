package jjhhyb.deepvalley.banner;

import jjhhyb.deepvalley.banner.dto.response.BannerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BannerServiceImpl implements BannerService {

    @Override
    public BannerResponse getBanner() {
        List<String> imageUrls = List.of(
                "https://example.com/banner1.jpg",
                "https://example.com/banner2.jpg",
                "https://example.com/banner3.jpg",
                "https://example.com/banner4.jpg",
                "https://example.com/banner5.jpg"
        );
        return new BannerResponse(imageUrls);
    }
}