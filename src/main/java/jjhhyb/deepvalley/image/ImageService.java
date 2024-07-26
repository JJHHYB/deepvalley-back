package jjhhyb.deepvalley.image;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final S3Service s3Service;
    private static final String REVIEW_IMAGE_FOLDER = "review-images";
    private static final String PROFILE_IMAGE_FOLDER = "profile-images";

    // 이미지 파일을 S3에 업로드하고 URL 리스트를 반환
    public List<String> uploadImagesAndGetUrls(List<MultipartFile> imageFiles, ImageType imageType) {
        String folder = getFolderByImageType(imageType);
        return imageFiles.stream()
                .map(file -> s3Service.uploadFile(file, folder))
                .collect(Collectors.toList());
    }

    // 주어진 리스트의 이미지 삭제
    public void deleteImages(List<String> imageUrls) {
        imageUrls.forEach(s3Service::deleteImage);
    }

    // 폴더 구분하기
    private String getFolderByImageType(ImageType imageType) {
        return switch (imageType) {
            case REVIEW -> REVIEW_IMAGE_FOLDER;
            case PROFILE -> PROFILE_IMAGE_FOLDER;
        };
    }
}
