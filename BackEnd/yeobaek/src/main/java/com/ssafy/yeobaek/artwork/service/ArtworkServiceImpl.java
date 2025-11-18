package com.ssafy.yeobaek.artwork.service;

import com.ssafy.yeobaek.artwork.dto.request.CreateArtworkRequest;
import com.ssafy.yeobaek.artwork.dto.request.UpdateArtworkRequest;
import com.ssafy.yeobaek.artwork.dto.response.*;
import com.ssafy.yeobaek.artwork.entity.Artwork;
import com.ssafy.yeobaek.artwork.entity.ArtworkBookmark;
import com.ssafy.yeobaek.artwork.mapper.ArtworkMapper;
import com.ssafy.yeobaek.artwork.repository.ArtworkBookmarkRepository;
import com.ssafy.yeobaek.artwork.repository.ArtworkRepository;
import com.ssafy.yeobaek.artwork.repository.ArtworkSpecification;
import com.ssafy.yeobaek.cafe.repository.DisplaySlotRepository;
import com.ssafy.yeobaek.common.code.ResponseCode;
import com.ssafy.yeobaek.common.dto.Pagination;
import com.ssafy.yeobaek.common.enums.ArtCategory;
import com.ssafy.yeobaek.common.enums.ArtworkSortBy;
import com.ssafy.yeobaek.common.enums.UserRole;
import com.ssafy.yeobaek.common.exception.CustomException;
import com.ssafy.yeobaek.exhibition.entity.Exhibition;
import com.ssafy.yeobaek.exhibition.repository.ExhibitionRepository;
import com.ssafy.yeobaek.exhibition.service.ExhibitionService;
import com.ssafy.yeobaek.storage.service.S3Service;
import com.ssafy.yeobaek.user.entity.Artist;
import com.ssafy.yeobaek.user.entity.User;
import com.ssafy.yeobaek.user.service.UserService;
import com.ssafy.yeobaek.recommendation.service.EmbeddingService;
import com.ssafy.yeobaek.recommendation.service.RecommendationService;
import com.ssafy.yeobaek.recommendation.dto.response.ArtworkHybridResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArtworkServiceImpl implements ArtworkService {

    private final ArtworkRepository artworkRepository;
    private final ArtworkBookmarkRepository artworkBookmarkRepository;
    private final ArtworkBookmarkService artworkBookmarkService;
    private final UserService userService;
    private final ArtworkMapper artworkMapper;
    private final S3Service s3Service;
    private final ExhibitionService exhibitionService;
    private final ExhibitionRepository exhibitionRepository;
    private final DisplaySlotRepository displaySlotRepository;
    private final EmbeddingService embeddingService;
    private final RecommendationService recommendationService;

    /**
     * 작품 등록
     * @param userId 사용자 ID
     * @param request 작품 등록 정보
     */
    @Override
    @Transactional
    public void createArtwork(Long userId, CreateArtworkRequest request) {
        log.info("작품 등록 시작 - 사용자 ID: {}, 작품명: {}", userId, request.getTitle());
        // Artist 프록시 객체 참조
        Artist artist = userService.getArtistReference(userId);

        // 카테고리 변환
        ArtCategory artCategory = ArtCategory.valueOf(request.getCategory());

        // Artwork 엔티티 생성 및 저장
        Artwork artwork = Artwork.builder()
                .artist(artist)
                .title(request.getTitle())
                .description(request.getDescription())
                .artCategory(artCategory)
                .price(request.getPrice())
                .widthCm(request.getWidthCm().doubleValue())
                .heightCm(request.getHeightCm().doubleValue())
                .weightKg(request.getWeightKg())
                .productionYear(request.getProductionYear())
                .images(request.getImages())
                .build();

        Artwork savedArtwork = artworkRepository.save(artwork);

        // 비동기로 임베딩 생성 및 저장
        embeddingService.generateAndSaveArtworkEmbedding(savedArtwork.getId());
        log.info("작품 등록 완료 - 작품 ID: {}", savedArtwork.getId());
    }

    /**
     * 작품 수정
     * @param userId 사용자 ID (권한 검증용)
     * @param artworkId 수정할 작품 ID
     * @param request 작품 수정 정보
     */
    @Override
    @Transactional
    public void updateArtwork(Long userId, Long artworkId, UpdateArtworkRequest request) {
        log.info("작품 수정 시작 - 사용자 ID: {}, 작품 ID: {}", userId, artworkId);
        // 작품 조회
        Artwork artwork = artworkRepository.findById(artworkId)
                .orElseThrow(() -> {
                    log.warn("작품 수정 실패 - 존재하지 않는 작품 ID: {}", artworkId);
                    return new CustomException(ResponseCode.ARTWORK_NOT_FOUND);
                });

        // 권한 검증: 본인의 작품만 수정 가능
        if (!artwork.getArtist().getUser().getId().equals(userId)) {
            log.warn("작품 수정 권한 없음 - 사용자 ID: {}, 작품 소유자 ID: {}", userId, artwork.getArtist().getUser().getId());
            throw new CustomException(ResponseCode.ARTWORK_FORBIDDEN);
        }

        // 전시 중인 작품의 규격 변경 검증
        boolean isSizeChanging = (request.getWidthCm() != null && !request.getWidthCm().equals(artwork.getWidthCm().intValue())) ||
                                 (request.getHeightCm() != null && !request.getHeightCm().equals(artwork.getHeightCm().intValue()));

        if (artwork.getIsExhibiting() && isSizeChanging) {
            log.warn("전시 중인 작품 규격 변경 시도 - 작품 ID: {}", artworkId);
            throw new CustomException(ResponseCode.ARTWORK_EXHIBITING_SIZE_CHANGE);
        }

        // 필드별 업데이트 (null이 아닌 필드만)
        if (request.getTitle() != null) {
            artwork.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            artwork.setDescription(request.getDescription());
        }
        if (request.getCategory() != null) {
            ArtCategory artCategory = ArtCategory.valueOf(request.getCategory());
            artwork.setArtCategory(artCategory);
        }
        if (request.getPrice() != null) {
            artwork.setPrice(request.getPrice());
        }
        if (request.getWidthCm() != null) {
            artwork.setWidthCm(request.getWidthCm().doubleValue());
        }
        if (request.getHeightCm() != null) {
            artwork.setHeightCm(request.getHeightCm().doubleValue());
        }
        if (request.getWeightKg() != null) {
            artwork.setWeightKg(request.getWeightKg());
        }
        if (request.getProductionYear() != null) {
            artwork.setProductionYear(request.getProductionYear());
        }
        if (request.getImages() != null) {
            artwork.setImages(request.getImages());
        }

        // 변경 사항 저장
        artworkRepository.save(artwork);
        log.info("작품 수정 완료 - 작품 ID: {}", artworkId);
    }

    /**
     * 작품 상세 조회
     * @param artworkId 작품 ID
     * @param userId 현재 사용자 ID (북마크 여부 확인용)
     * @return 작품 상세 정보
     */
    @Override
    @Transactional
    public ArtworkDetailResponse getArtworkDetail(Long artworkId, Long userId) {
        log.info("작품 상세 조회 시작 - 작품 ID: {}, 사용자 ID: {}", artworkId, userId);
        // 작품 조회 (없으면 예외 발생)
        Artwork artwork = artworkRepository.findById(artworkId)
                .orElseThrow(() -> {
                    log.warn("작품 상세 조회 실패 - 존재하지 않는 작품 ID: {}", artworkId);
                    return new CustomException(ResponseCode.ARTWORK_NOT_FOUND);
                });

        // 조회수 증가
        artwork.incrementViewCount();

        // 북마크 여부 확인
        boolean isBookmarked = artworkBookmarkService.isBookmarked(userId, artworkId);

        // S3 presigned URL 변환
        List<String> artworkImageUrls = s3Service.generatePresignedUrlsForDownload(artwork.getImages());
        String artistProfileImageUrl = s3Service.generatePresignedUrlForDownload(artwork.getArtist().getUser().getProfileImageUrl());

        // 현재 진행 중인 전시 조회
        ExhibitionDto exhibitionDto = null;
        Optional<Exhibition> currentExhibition = exhibitionService.getCurrentExhibitionByArtwork(artworkId);

        if (currentExhibition.isPresent()) {
            // 카페 대표 이미지 URL 변환
            String cafeImageUrl = s3Service.generatePresignedUrlForDownload(currentExhibition.get().getCafe().getImages().get(0));
            // Exhibition 엔티티를 DTO로 변환
            exhibitionDto = artworkMapper.toExhibition(currentExhibition.get(), cafeImageUrl);
        }

        // DTO로 변환하여 반환
        ArtworkDetailResponse response = artworkMapper.toArtworkDetailData(artwork, isBookmarked, artworkImageUrls, artistProfileImageUrl, exhibitionDto);
        log.info("작품 상세 조회 완료 - 작품 ID: {}", artworkId);
        return response;
    }

    /**
     * 작품 검색 (키워드, 필터, 정렬)
     * 키워드가 있으면 작가/작품 통합 검색을 수행하고, 없으면 필터링과 정렬만 적용합니다.
     * 규격 필터(가로/세로)는 카페 사장만 사용할 수 있습니다.
     * @param userId 현재 사용자 ID (권한 검증용, null 가능)
     * @param keyword 작품명/작가명 검색 키워드 (null 가능)
     * @param categories 카테고리 필터 목록 (null 가능)
     * @param minWidth 최소 가로 크기 (cm 단위, null 가능)
     * @param maxWidth 최대 가로 크기 (cm 단위, null 가능)
     * @param minHeight 최소 세로 크기 (cm 단위, null 가능)
     * @param maxHeight 최대 세로 크기 (cm 단위, null 가능)
     * @param sortBy 정렬 기준 (LATEST: 최신순, POPULARITY: 인기순)
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 작품 목록 및 작가 목록 (키워드가 있으면 작가 목록 포함, 없으면 null)
     */
    @Override
    @Transactional(readOnly = true)
    public ArtworkListResponse searchArtworks(
            Long userId,
            String keyword,
            List<ArtCategory> categories,
            Integer minWidth,
            Integer maxWidth,
            Integer minHeight,
            Integer maxHeight,
            ArtworkSortBy sortBy,
            Integer page,
            Integer size
    ) {
        log.info("작품 검색 시작 - 사용자 ID: {}, 키워드: '{}', 카테고리: {}, 규격: [w: {}-{}, h: {}-{}], 정렬: {}, 페이지: {}, 사이즈: {}",
                userId, keyword, categories, minWidth, maxWidth, minHeight, maxHeight, sortBy, page, size);
        // 규격 필터 권한 검증 (카페 사장만 사용 가능)
        validateSpecFilter(userId, minWidth, maxWidth, minHeight, maxHeight);

        // Pageable 생성
        Pageable pageable = PageRequest.of(page, size);

        // Specification을 사용한 작품 검색
        Specification<Artwork> spec = Specification
                .where(ArtworkSpecification.withFilters(keyword, categories, minWidth, maxWidth, minHeight, maxHeight))
                .and(ArtworkSpecification.withSort(sortBy));

        Page<Artwork> artworkPage = artworkRepository.findAll(spec, pageable);

        // 작품을 ArtworkSummary로 변환
        List<ArtworkSummaryDto> artworkSummaries = convertToArtworkSummaries(artworkPage.getContent());

        // 키워드가 있으면 작가 검색도 수행
        List<ArtistDto> artistRespons = new ArrayList<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            artistRespons = searchArtistsByKeyword(keyword);
        }

        // Pagination DTO 생성
        Pagination pagination = artworkMapper.toPagination(artworkPage);

        ArtworkListResponse response = artworkMapper.toArtworkListData(artistRespons, artworkSummaries, pagination);
        log.info("작품 검색 완료 - 조회된 작품 수: {}", artworkPage.getNumberOfElements());
        return response;
    }

    /**
     * 규격 필터 권한 검증 (카페 사장만 사용 가능)
     */
    private void validateSpecFilter(Long userId, Integer minWidth, Integer maxWidth, Integer minHeight, Integer maxHeight) {
        boolean hasSpecFilter = minWidth != null || maxWidth != null || minHeight != null || maxHeight != null;
        if (hasSpecFilter && userId != null) {
            User user = userService.findById(userId)
                    .orElseThrow(() -> {
                        log.warn("규격 필터 검증 실패 - 존재하지 않는 사용자 ID: {}", userId);
                        return new CustomException(ResponseCode.PROFILE_USER_NOT_FOUND);
                    });

            if (user.getUserType() != UserRole.CAFE_OWNER) {
                log.warn("규격 필터 사용 권한 없음 - 사용자 ID: {}, 사용자 유형: {}", userId, user.getUserType());
                throw new CustomException(ResponseCode.ARTWORK_SIZE_FILTER_FORBIDDEN);
            }
        }
    }

    /**
     * 작품 엔티티를 ArtworkSummary DTO로 변환 (S3 presigned URL 포함)
     */
    private List<ArtworkSummaryDto> convertToArtworkSummaries(List<Artwork> artworks) {
        return artworks.stream()
                .map(artwork -> {
                    // 썸네일 URL 변환 (첫 번째 이미지 사용)
                    String thumbnailUrl = null;
                    if (artwork.getImages() != null && !artwork.getImages().isEmpty()) {
                        thumbnailUrl = s3Service.generatePresignedUrlForThumbnail(artwork.getImages().get(0));
                    }
                    return artworkMapper.toArtworkSummary(artwork, thumbnailUrl);
                })
                .collect(Collectors.toList());
    }

    /**
     * 키워드로 작가 검색 및 Artist DTO로 변환 (S3 presigned URL 포함)
     */
    private List<ArtistDto> searchArtistsByKeyword(String keyword) {
        List<Artist> artists = userService.searchArtistsByKeyword(keyword);

        return artists.stream()
                .map(artist -> {
                    int artworkCount = artworkRepository.countByArtist(artist);

                    // 프로필 이미지 URL을 presigned URL로 변환
                    String profileImageUrl = null;
                    if (artist.getUser().getProfileImageUrl() != null && !artist.getUser().getProfileImageUrl().isEmpty()) {
                        profileImageUrl = s3Service.generatePresignedUrlForThumbnail(artist.getUser().getProfileImageUrl());
                    }

                    return artworkMapper.toArtist(artist, artworkCount, profileImageUrl);
                })
                .collect(Collectors.toList());
    }

    /**
     * 특정 사용자의 작품 목록 조회
     * @param userId 조회할 사용자 ID
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 작품 목록 및 페이징 정보
     */
    @Override
    @Transactional(readOnly = true)
    public ArtworkListResponse getUserArtworks(Long userId, Integer page, Integer size) {
        log.info("특정 사용자 작품 목록 조회 시작 - 사용자 ID: {}, 페이지: {}, 사이즈: {}", userId, page, size);
        // Artist 엔티티 조회
        Artist artist = userService.getArtistReference(userId);

        // Pageable 생성
        Pageable pageable = PageRequest.of(page, size);

        // 작가별 작품 목록 조회 (페이징)
        Page<Artwork> artworkPage = artworkRepository.findByArtist(artist, pageable);

        // 작품을 ArtworkSummary로 변환
        List<ArtworkSummaryDto> artworkSummaries = artworkPage.getContent().stream()
                .map(artwork -> {
                    // 썸네일 URL 변환 (첫 번째 이미지 사용)
                    String thumbnailUrl = null;
                    if (artwork.getImages() != null && !artwork.getImages().isEmpty()) {
                        thumbnailUrl = s3Service.generatePresignedUrlForThumbnail(artwork.getImages().get(0));
                    }
                    return artworkMapper.toArtworkSummaryWithDetails(artwork, thumbnailUrl);
                })
                .collect(Collectors.toList());

        // Pagination DTO 생성
        Pagination pagination = artworkMapper.toPagination(artworkPage);

        // artists는 null로 설정 (작가 검색이 아니므로)
        ArtworkListResponse response = artworkMapper.toArtworkListData(null, artworkSummaries, pagination);
        log.info("특정 사용자 작품 목록 조회 완료 - 조회된 작품 수: {}", artworkPage.getNumberOfElements());
        return response;
    }

    /**
     * 작가 홈 조회 (인기 작품 상위 3개 + 작가 프로필)
     * - 인기도 기준: 조회수 × 1 + 북마크수 × 9
     * - 전시 중인 작품은 카페명 포함
     * - 모든 작품에 작가 프로필 정보 포함
     * @param userId 작가 ID
     * @return 인기 작품 목록 (최대 3개)
     */
    @Override
    @Transactional(readOnly = true)
    public ArtistHomeResponse getArtistHome(Long userId) {
        log.info("작가 홈 조회 시작 - 작가 ID: {}", userId);
        // 1. Artist 엔티티 조회
        Artist artist = userService.getArtistReference(userId);

        // 2. 작가 통계 조회
        Integer artworkCount = artworkRepository.countByArtist(artist);
        Integer currentExhibitionCount = exhibitionService.getCurrentExhibitionCountByArtist(userId);

        // 3. 작가 프로필 이미지 URL 변환
        String profileImageUrl = s3Service.generatePresignedUrlForThumbnail(artist.getUser().getProfileImageUrl());

        // 4. ArtistDto 생성
        ArtistDto artistDto = artworkMapper.toArtistDtoForArtistHome(artist, profileImageUrl, artworkCount, currentExhibitionCount);

        // 5. 인기 작품 상위 3개 조회 (조회수 1 + 북마크수 9 가중치)
        Pageable top3 = PageRequest.of(0, 3);
        List<Artwork> popularArtworks = artworkRepository.findTopPopularByArtistId(userId, top3);

        // 6. 작품 DTO 변환
        List<ArtworkSummaryDto> artworkSummaries = popularArtworks.stream()
                .map(artwork -> {
                    // 썸네일 URL 변환
                    String thumbnailUrl = null;
                    if (artwork.getImages() != null && !artwork.getImages().isEmpty()) {
                        thumbnailUrl = s3Service.generatePresignedUrlForThumbnail(artwork.getImages().get(0));
                    }

                    // 전시 중이면 카페명 조회
                    String cafeName = null;
                    if (artwork.getIsExhibiting()) {
                        Optional<Exhibition> exhibition = exhibitionService.getCurrentExhibitionByArtwork(artwork.getId());
                        cafeName = exhibition.map(e -> e.getCafe().getCafeName()).orElse(null);
                    }

                    // DTO 변환
                    return artworkMapper.toArtworkSummaryDtoForArtistHome(artwork, thumbnailUrl, cafeName);
                })
                .collect(Collectors.toList());

        ArtistHomeResponse response = artworkMapper.toArtistHomeResponse(artistDto, artworkSummaries);
        log.info("작가 홈 조회 완료 - 작가 ID: {}, 인기 작품 수: {}", userId, artworkSummaries.size());
        return response;
    }

    /**
     * 북마크한 작품 목록 조회
     * @param userId 사용자 ID
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 북마크한 작품 목록 및 페이징 정보
     */
    @Override
    @Transactional(readOnly = true)
    public ArtworkListResponse getBookmarkedArtworks(Long userId, Integer page, Integer size) {
        log.info("북마크 작품 목록 조회 시작 - 사용자 ID: {}, 페이지: {}, 사이즈: {}", userId, page, size);
        // Pageable 생성
        Pageable pageable = PageRequest.of(page, size);

        // 북마크한 작품 목록 조회 (페이징)
        Page<ArtworkBookmark> bookmarkPage = artworkBookmarkRepository.findByUser_IdOrderByCreatedAtDesc(userId, pageable);

        // 북마크한 작품을 ArtworkSummaryDto로 변환
        List<ArtworkSummaryDto> artworkSummaries = bookmarkPage.getContent().stream()
                .map(bookmark -> {
                    Artwork artwork = bookmark.getArtwork();
                    Artist artist = artwork.getArtist();

                    // 썸네일 URL 변환 (첫 번째 이미지 사용)
                    String thumbnailUrl = s3Service.generatePresignedUrlForThumbnail(artwork.getImages().get(0));

                    // 작가 프로필 이미지 URL 변환
                    String profileImageUrl = s3Service.generatePresignedUrlForThumbnail(artist.getUser().getProfileImageUrl());

                    // 작가 정보 DTO 생성 (기본 정보만)
                    ArtistDto artistDto = artworkMapper.toArtist(artist, profileImageUrl);

                    // 전시 중이면 카페명 조회
                    String cafeName = null;
                    if (artwork.getIsExhibiting()) {
                        Optional<Exhibition> exhibition = exhibitionService.getCurrentExhibitionByArtwork(artwork.getId());
                        cafeName = exhibition.map(e -> e.getCafe().getCafeName()).orElse(null);
                    }

                    // DTO 변환
                    return artworkMapper.toArtworkSummaryDtoForBookmark(artwork, thumbnailUrl, cafeName, artistDto);
                })
                .collect(Collectors.toList());

        // Pagination DTO 생성
        Pagination pagination = artworkMapper.toPagination(bookmarkPage);

        // artists는 null로 설정 (북마크 목록이므로)
        ArtworkListResponse response = artworkMapper.toArtworkListData(null, artworkSummaries, pagination);
        log.info("북마크 작품 목록 조회 완료 - 조회된 작품 수: {}", bookmarkPage.getNumberOfElements());
        return response;
    }

    /**
     * 인기 작품 목록 조회
     * @param size 조회할 작품 수 (최대 20개)
     * @return 인기 작품 목록
     */
    @Override
    @Transactional(readOnly = true)
    public ArtworkListResponse getPopularArtworks(Integer size) {
        log.info("인기 작품 목록 조회 시작 - 사이즈: {}", size);
        // Pageable 생성
        Pageable pageable = PageRequest.of(0, size);

        // 인기 작품 조회 (조회수 1 + 북마크수 9 가중치)
        List<Artwork> popularArtworks = artworkRepository.findTopPopularArtworks(pageable);

        // 작품을 ArtworkSummaryDto로 변환
        List<ArtworkSummaryDto> artworkSummaries = popularArtworks.stream()
                .map(artwork -> {
                    Artist artist = artwork.getArtist();

                    // 썸네일 URL 변환 (첫 번째 이미지 사용)
                    String thumbnailUrl = s3Service.generatePresignedUrlForThumbnail(artwork.getImages().get(0));

                    // 작가 프로필 이미지 URL 변환
                    String profileImageUrl = s3Service.generatePresignedUrlForThumbnail(artist.getUser().getProfileImageUrl());

                    // 작가 정보 DTO 생성 (기본 정보만)
                    ArtistDto artistDto = artworkMapper.toArtist(artist, profileImageUrl);

                    // 전시 중이면 카페명 조회
                    String cafeName = null;
                    if (artwork.getIsExhibiting()) {
                        Optional<Exhibition> exhibition = exhibitionService.getCurrentExhibitionByArtwork(artwork.getId());
                        cafeName = exhibition.map(e -> e.getCafe().getCafeName()).orElse(null);
                    }

                    // DTO 변환
                    return artworkMapper.toArtworkSummaryDtoForBookmark(artwork, thumbnailUrl, cafeName, artistDto);
                })
                .collect(Collectors.toList());

        // artists와 pagination은 null로 설정 (인기 작품 목록이므로)
        ArtworkListResponse response = artworkMapper.toArtworkListData(null, artworkSummaries, null);
        log.info("인기 작품 목록 조회 완료 - 조회된 작품 수: {}", artworkSummaries.size());
        return response;
    }

    /**
     * 추천 작품 조회 (하이브리드 추천: CF + CBF)
     * @param slotId 슬롯 ID
     * @return 추천 작품 목록 (상위 10개)
     */
    @Override
    @Transactional(readOnly = true)
    public ArtworkListResponse getRecommendedArtworks(Long slotId) {
        log.info("추천 작품 조회 시작 - 슬롯 ID: {}", slotId);
        // 1. 슬롯 존재 여부 확인
        if (!displaySlotRepository.existsById(slotId)) {
            log.warn("추천 작품 조회 실패 - 존재하지 않는 슬롯 ID: {}", slotId);
            throw new CustomException(ResponseCode.SLOT_NOT_FOUND);
        }

        try {
            // 파이썬 추천 서버로부터 추천 작품 ID 목록 받기 (CF + CBF 하이브리드)
            int topN = 10;
            ArtworkHybridResponse recommendationResponse = recommendationService.recommendArtworksForSlot(slotId, topN);

            // 추천된 작품 ID 리스트 추출
            List<Long> recommendedArtworkIds = recommendationResponse.getRecommendations().stream()
                    .map(ArtworkHybridResponse.ArtworkItem::getArtworkId)
                    .toList();
            log.info("추천 서버로부터 작품 추천 응답 받음 - 추천된 작품 수: {}", recommendedArtworkIds.size());

            // DB에서 작품 조회 (추천 순서 유지)
            List<Artwork> artworks = recommendedArtworkIds.stream()
                    .map(id -> artworkRepository.findById(id).orElse(null))
                    .filter(Objects::nonNull)
                    .toList();

            // 작품을 ArtworkSummaryDto로 변환
            List<ArtworkSummaryDto> artworkSummaries = artworks.stream()
                    .map(artwork -> {
                        // 썸네일 URL 변환 (첫 번째 이미지 사용)
                        String thumbnailUrl = null;
                        if (artwork.getImages() != null && !artwork.getImages().isEmpty()) {
                            thumbnailUrl = s3Service.generatePresignedUrlForThumbnail(artwork.getImages().get(0));
                        }
                        return artworkMapper.toArtworkSummaryWithDetails(artwork, thumbnailUrl);
                    })
                    .collect(Collectors.toList());

            // artists는 null로 설정 (추천 목록이므로), pagination도 null
            ArtworkListResponse response = artworkMapper.toArtworkListData(null, artworkSummaries, null);
            log.info("추천 작품 조회 완료 - 슬롯 ID: {}", slotId);
            return response;

        } catch (Exception e) {
            log.error("Failed to get artwork recommendations from Python server for slot {}", slotId, e);
            // 폴백: 인기순으로 작품 조회
            log.info("Falling back to popularity-based recommendations");
            return getFallbackRecommendations();
        }
    }

    /**
     * 추천 시스템 실패 시 폴백 로직 (인기순 상위 10개)
     */
    private ArtworkListResponse getFallbackRecommendations() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Artwork> artworkPage = artworkRepository.findTopPopularArtworksPage(pageable);

        List<ArtworkSummaryDto> artworkSummaries = artworkPage.getContent().stream()
                .map(artwork -> {
                    String thumbnailUrl = null;
                    if (artwork.getImages() != null && !artwork.getImages().isEmpty()) {
                        thumbnailUrl = s3Service.generatePresignedUrlForThumbnail(artwork.getImages().get(0));
                    }
                    return artworkMapper.toArtworkSummaryWithDetails(artwork, thumbnailUrl);
                })
                .collect(Collectors.toList());

        return artworkMapper.toArtworkListData(null, artworkSummaries, null);
    }

    /**
     * 작품의 전시 가능일 조회
     * - 조회 시작일로부터 30일 동안의 가능한 날짜 목록 반환
     * - 승인됨(APPROVED) 또는 진행 중(ONGOING) 상태의 전시와 겹치지 않는 날짜만 반환
     * @param artworkId 작품 ID
     * @param startDate 조회 시작일 (null이면 오늘 날짜 사용)
     * @return 전시 가능한 날짜 목록
     */
    @Override
    @Transactional(readOnly = true)
    public ArtworkAvailableDatesResponse getArtworkAvailableDates(Long artworkId, LocalDate startDate) {
        log.info("작품 전시 가능일 조회 시작 - 작품 ID: {}, 시작일: {}", artworkId, startDate);
        // 1. 작품 존재 여부 확인
        if (!artworkRepository.existsById(artworkId)) {
            log.warn("작품 전시 가능일 조회 실패 - 존재하지 않는 작품 ID: {}", artworkId);
            throw new CustomException(ResponseCode.ARTWORK_NOT_FOUND);
        }

        // 2. 시작일 설정 및 검증 (null이면 오늘 날짜 사용, 오늘 이전이면 에러)
        LocalDate today = LocalDate.now();
        LocalDate queryStartDate = (startDate != null) ? startDate : today;
        if (queryStartDate.isBefore(today)) {
            log.warn("작품 전시 가능일 조회 실패 - 조회 시작일이 오늘보다 이전입니다: {}", queryStartDate);
            throw new CustomException(ResponseCode.INVALID_INPUT_VALUE);
        }

        // 3. 종료일 계산 (시작일 + 30일)
        LocalDate queryEndDate = queryStartDate.plusDays(30);

        // 4. 해당 기간 내 모든 날짜를 Set에 추가
        Set<LocalDate> allDates = new HashSet<>();
        LocalDate currentDate = queryStartDate;
        while (!currentDate.isAfter(queryEndDate)) {
            allDates.add(currentDate);
            currentDate = currentDate.plusDays(1);
        }

        // 5. 해당 기간과 겹치는 전시 목록 조회 (APPROVED, ONGOING만)
        List<Exhibition> exhibitions = exhibitionRepository.findByArtworkIdAndDateRange(artworkId, queryStartDate, queryEndDate);

        // 6. 전시 기간에 포함되는 날짜를 Set에서 제거
        for (Exhibition exhibition : exhibitions) {
            LocalDate exhibitionDate = exhibition.getStartDate();
            while (!exhibitionDate.isAfter(exhibition.getEndDate())) {
                allDates.remove(exhibitionDate);
                exhibitionDate = exhibitionDate.plusDays(1);
            }
        }

        // 7. 남은 날짜들을 정렬하여 리스트로 변환
        List<LocalDate> availableDates = allDates.stream()
                .sorted()
                .collect(Collectors.toList());

        // 8. DTO 변환 및 반환
        ArtworkAvailableDatesResponse response = artworkMapper.toArtworkAvailableDatesResponse(availableDates);
        log.info("작품 전시 가능일 조회 완료 - 작품 ID: {}, 가능한 날짜 수: {}", artworkId, availableDates.size());
        return response;
    }

    /**
     * 전시 가능한 작품 목록 조회
     * - 특정 작가의 특정 기간에 전시 가능한 작품 목록 반환
     * - 요청된 기간에 전시가 없는 작품만 반환 (APPROVED 또는 ONGOING 상태의 전시와 겹치지 않음)
     * @param artistId 작가 ID
     * @param startDate 조회 시작일
     * @param endDate 조회 종료일
     * @return 전시 가능한 작품 목록
     */
    @Override
    @Transactional(readOnly = true)
    public ArtworkListResponse getAvailableArtworks(Long artistId, LocalDate startDate, LocalDate endDate) {
        log.info("전시 가능한 작품 목록 조회 시작 - 작가 ID: {}, 기간: {} ~ {}", artistId, startDate, endDate);
        // 1. 작가 존재 여부 확인
        if (!userService.existsArtistById(artistId)) {
            log.warn("전시 가능한 작품 목록 조회 실패 - 존재하지 않는 작가 ID: {}", artistId);
            throw new CustomException(ResponseCode.PROFILE_USER_NOT_FOUND);
        }

        // 2. 작가의 모든 작품 조회
        List<Artwork> allArtworks = artworkRepository.findAllByArtistId(artistId);

        // 3. 전시가 없는 작품만 필터링
        List<Artwork> availableArtworks = allArtworks.stream()
                .filter(artwork -> !exhibitionRepository.existsConflictingArtwork(artwork.getId(), startDate, endDate))
                .collect(Collectors.toList());

        // 4. 작품을 ArtworkSummaryDto로 변환 (기본 정보만 포함)
        List<ArtworkSummaryDto> artworkSummaries = availableArtworks.stream()
                .map(artwork -> {
                    // 썸네일 URL 변환 (첫 번째 이미지 사용)
                    String thumbnailUrl = s3Service.generatePresignedUrlForThumbnail(artwork.getImages().get(0));
                    return artworkMapper.toArtworkSummaryForAvailableArtworks(artwork, thumbnailUrl);
                })
                .collect(Collectors.toList());

        // 5. artists와 pagination은 null로 설정 (전시 가능한 작품 목록이므로)
        ArtworkListResponse response = artworkMapper.toArtworkListData(null, artworkSummaries, null);
        log.info("전시 가능한 작품 목록 조회 완료 - 작가 ID: {}, 가능한 작품 수: {}", artistId, artworkSummaries.size());
        return response;
    }
}
