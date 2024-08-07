package jjhhyb.deepvalley.suggest;

import jakarta.persistence.EntityNotFoundException;
import jjhhyb.deepvalley.place.Place;
import jjhhyb.deepvalley.place.PlaceRepository;
import jjhhyb.deepvalley.user.entity.Member;
import jjhhyb.deepvalley.user.repository.MemberRepository;
import jjhhyb.deepvalley.image.ImageService;
import jjhhyb.deepvalley.image.ImageType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SuggestService {
    private final SuggestRepository suggestRepository;
    private final SuggestImageService suggestImageService;
    private final ImageService imageService;
    private final SuggestImageRepository suggestImageRepository;
    private final MemberRepository memberRepository;
    private final PlaceRepository placeRepository;

    private static final String USER_NOT_FOUND = "사용자를 찾을 수 없습니다.";
    private static final String SUGGEST_NOT_FOUND = "제안을 찾을 수 없습니다.";
    private static final String INVALID_PLACE_ID = "유효하지 않은 장소 ID입니다.";
    private static final String INVALID_DATE_FORMAT = "유효하지 않은 날짜 형식 : ";
    private static final String NOT_USER_REVIEW = "사용자가 작성한 리뷰가 아닙니다.";

    @Transactional
    public SuggestDetailResponse createSuggest(SuggestPostRequest request, List<MultipartFile> imageFiles, String userId) {
        // userId를 이용하여 Member 엔티티 조회
        Member member = findMemberByUserId(userId);

        // Suggest 엔티티 생성
        Suggest suggest = createSuggestEntity(request, member);

        // 생성한 Suggest 엔티티 데이터베이스에 저장
        Suggest savedSuggest = suggestRepository.save(suggest);

        if (imageFiles != null && !imageFiles.isEmpty()) {
            // 이미지 파일 업로드 및 URL 생성
            List<String> imageUrls = imageService.uploadImagesAndGetUrls(imageFiles, ImageType.SUGGEST);

            // 이미지 처리
            List<SuggestImage> suggestImages = suggestImageService.processImages(imageUrls, savedSuggest);
            updateSuggestWithImages(savedSuggest, suggestImages);
        }

        // 응답 객체로 변환 후 반환
        return SuggestDetailResponse.from(savedSuggest);
    }

    // 제안 업데이트
    @Transactional
    public SuggestDetailResponse updateSuggest(String suggestId, SuggestPostRequest request, List<MultipartFile> imageFiles, String userId) {
        // 제안 존재 여부 및 작성자 확인
        Suggest updateSuggest = validateSuggestOwner(suggestId, userId);

        // 기존 제안 이미지 URL 목록 가져오기
        List<String> existingImageUrls = updateSuggest.getSuggestImages().stream()
                .map(suggestImage -> suggestImage.getImage().getImageUrl())
                .toList();

        // 제안 엔티티 업데이트
        updateSuggestEntity(updateSuggest, request);

        if (imageFiles != null && !imageFiles.isEmpty()) {
            // 이미지 파일 업로드 및 URL 생성 & 새로운 SuggestImage 객체 생성
            List<String> newImageUrls = imageService.uploadImagesAndGetUrls(imageFiles, ImageType.SUGGEST);
            List<SuggestImage> updatedSuggestImages = suggestImageService.processImages(newImageUrls, updateSuggest);

            // 요청된 이미지 URL을 Set으로 변환하여 기존 이미지와 비교
            Set<String> newImageUrlSet = new HashSet<>(newImageUrls);

            // 삭제할 이미지 결정: 기존 이미지 중 요청된 이미지 URL에 없는 이미지
            List<SuggestImage> imagesToDelete = updateSuggest.getSuggestImages().stream()
                    .filter(existingSuggestImage -> !newImageUrlSet.contains(existingSuggestImage.getImage().getImageUrl()))
                    .collect(Collectors.toList());

            // 기존 이미지 삭제
            if (!imagesToDelete.isEmpty()) {
                suggestImageService.deleteAll(imagesToDelete);
            }

            // 새 이미지 추가
            suggestImageService.updateSuggestImages(updateSuggest, updatedSuggestImages);
        } else {
            // 이미지 파일이 없는 경우: 기존 이미지가 있다면 삭제 처리
            if (!existingImageUrls.isEmpty()) {
                List<SuggestImage> imagesToDelete = updateSuggest.getSuggestImages();
                if (!imagesToDelete.isEmpty()) {
                    suggestImageService.deleteAll(imagesToDelete);
                }
            }
        }

        // 업데이트된 제안 저장
        suggestRepository.save(updateSuggest);

        return SuggestDetailResponse.from(updateSuggest);
    }

    @Transactional
    public void deleteSuggest(String suggestId, String userId) {
        // 제안 존재 여부 및 작성자 확인
        Suggest suggest = validateSuggestOwner(suggestId, userId);

        // 제안과 연관된 모든 이미지 삭제
        List<SuggestImage> suggestImages = suggestImageRepository.findBySuggest_SuggestId(suggest.getSuggestId());
        suggestImageService.deleteAll(suggestImages);

        // 제안 삭제
        suggestRepository.delete(suggest);
    }

    @Transactional(readOnly = true)
    public SuggestsResponse getSuggests() {
        // 데이터베이스에서 모든 제안 목록을 조회
        List<Suggest> suggests = suggestRepository.findAll();

        // Suggest 엔터티를 SuggestResponse로 변환
        List<SuggestDetailResponse> suggestDetailResponses = suggests.stream()
                .map(SuggestDetailResponse::from)
                .collect(Collectors.toList());

        // SuggestsResponse 객체에 변환된 제안 목록을 설정
        SuggestsResponse suggestsResponse = new SuggestsResponse();
        suggestsResponse.setSuggests(suggestDetailResponses);

        return suggestsResponse;
    }

    @Transactional(readOnly = true)
    public SuggestDetailResponse getSuggestDetail(String suggestId) {
        // 제안이 존재하지 않으면 예외 처리
        Suggest suggest = suggestRepository.findByUuid(suggestId)
                .orElseThrow(() -> new SuggestNotFoundException(SUGGEST_NOT_FOUND));

        // Suggest 엔터티를 SuggestDetailResponse로 변환
        return SuggestDetailResponse.from(suggest);
    }

    // userId를 이용하여 Member 엔티티 조회
    private Member findMemberByUserId(String userId) {
        return memberRepository.findByLoginEmail(userId)
                .orElseThrow(() -> new IllegalArgumentException(USER_NOT_FOUND));
    }

    // 제안 존재 여부 및 작성자 확인
    private Suggest validateSuggestOwner(String suggestId, String userId) {
        // 제안 존재 여부 확인
        Suggest suggest = suggestRepository.findByUuid(suggestId)
                .orElseThrow(() -> new EntityNotFoundException(SUGGEST_NOT_FOUND + " with id: " + suggestId));
        // userId를 이용하여 Member 엔티티 조회
        Member member = findMemberByUserId(userId);
        // 작성자 검증
        if (!suggest.getMember().getMemberId().equals(member.getMemberId())) {
            throw new IllegalArgumentException(NOT_USER_REVIEW);
        }
        return suggest;
    }

    // 제안 엔티티 생성
    private Suggest createSuggestEntity(SuggestPostRequest request, Member member) {
        //LocalDate visitedDate = parseVisitedDate(request.getVisitedDate());

        // Place 엔티티를 ID로 조회
        Place place = placeRepository.findByUuid(request.getPlaceId())
                .orElseThrow(() -> new IllegalArgumentException(INVALID_PLACE_ID));

        return Suggest.builder()
                .uuid(UUID.randomUUID().toString())
                .title(request.getTitle())
                .content(request.getContent())
                .member(member)
                .place(place)
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();
    }

    // 제안에 이미지 추가 및 업데이트
    private void updateSuggestWithImages(Suggest suggest, List<SuggestImage> suggestImages) {
        suggest.setSuggestImages(suggestImages);
        suggestRepository.save(suggest);
        suggestImageRepository.saveAll(suggestImages);
    }

    private void updateSuggestEntity(Suggest suggest, SuggestPostRequest request) {
        suggest.setTitle(request.getTitle());
        suggest.setContent(request.getContent());
        suggest.setUpdatedDate(LocalDateTime.now());
    }

    private LocalDate parseVisitedDate(String visitedDateStr) {
        if (visitedDateStr == null || visitedDateStr.isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(visitedDateStr);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(INVALID_DATE_FORMAT + visitedDateStr, e);
        }
    }
}