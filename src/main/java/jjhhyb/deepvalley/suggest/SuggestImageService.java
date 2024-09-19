package jjhhyb.deepvalley.suggest;

import jjhhyb.deepvalley.community.repository.ImageRepository;
import jjhhyb.deepvalley.image.Image;
import jjhhyb.deepvalley.image.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SuggestImageService{
    private final ImageRepository imageRepository;
    private final SuggestImageRepository suggestImageRepository;
    private final ImageService imageService;

    // 이미지 URL 리스트로 SuggestImage 객체 리스트 생성
    public List<SuggestImage> processImages(List<String> imageUrls, Suggest suggest) {
        return imageUrls.stream()
                .map(imageUrl -> createOrUpdateImage(imageUrl, suggest))
                .collect(Collectors.toList());
    }

    // 이미지 URL로 Image 객체 생성 or 업데이트
    private SuggestImage createOrUpdateImage(String imageUrl, Suggest suggest) {
        // 데이터베이스에서 이미지 조회, 없으면 새로 생성
        Image image = imageRepository.findByImageUrl(imageUrl);
        if (image == null) {
            image = imageRepository.save(new Image(imageUrl));
        }
        // SuggestImage 객체 생성
        return SuggestImage.builder()
                .id(new SuggestImageId(suggest.getSuggestId(), image.getImageId()))
                .suggest(suggest)
                .image(image)
                .build();
    }

    // 제안과 연결된 이미지 업데이트
    public void updateSuggestImages(Suggest suggest, List<SuggestImage> updatedImages) {
        Set<Long> updatedImageIds = updatedImages.stream()
                .map(suggestImage -> suggestImage.getId().getImageId())
                .collect(Collectors.toSet());

        // 기존 이미지 리스트와 업데이트된 이미지 IDs를 비교하여 삭제할 이미지들을 결정
        List<SuggestImage> existingImages = new ArrayList<>(suggest.getSuggestImages());   // 모든 기존 이미지
        List<SuggestImage> imagesToDelete = existingImages.stream()                      // 더 이상 사용되지 않는 이미지를 필터링해 포함
                .filter(existingImage -> !updatedImageIds.contains(existingImage.getId().getImageId()))
                .toList();

        // 기존 이미지를 제안과의 연관관계에서 제거
        suggest.getSuggestImages().removeAll(existingImages);
        suggestImageRepository.flush();

        suggest.getSuggestImages().clear();
        suggest.getSuggestImages().addAll(updatedImages);

        // S3에서 이미지 삭제
        imageService.deleteImages(imagesToDelete.stream()
                .map(image -> image.getImage().getImageUrl())
                .collect(Collectors.toList()));
        // 데이터 베이스에서 삭제
        suggestImageRepository.deleteAll(existingImages);
    }

    // 주어진 SuggestImage 리스트의 모든 이미지 삭제
    public void deleteAll(List<SuggestImage> suggestImages) {
        imageService.deleteImages(suggestImages.stream()
                .map(image -> image.getImage().getImageUrl())
                .collect(Collectors.toList()));
        suggestImageRepository.deleteAll(suggestImages);
    }
}