package jjhhyb.deepvalley.banner;

import jjhhyb.deepvalley.banner.dto.response.BannerResponse;
import jjhhyb.deepvalley.image.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BannerServiceImpl implements BannerService {
    private final S3Service s3Service;

    @Override
    public BannerResponse getBanner() {
        List<String> imageUrls = s3Service.listFilesInFolder("banner-images");
        return new BannerResponse(imageUrls);
    }
}