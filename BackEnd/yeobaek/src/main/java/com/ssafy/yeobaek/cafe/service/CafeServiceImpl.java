package com.ssafy.yeobaek.cafe.service;

import com.ssafy.yeobaek.artwork.entity.Artwork;
import com.ssafy.yeobaek.artwork.repository.ArtworkRepository;
import com.ssafy.yeobaek.cafe.dto.request.UpdateCafeRequest;
import com.ssafy.yeobaek.cafe.dto.request.UpdateSlotRequest;
import com.ssafy.yeobaek.common.dto.Pagination;
import com.ssafy.yeobaek.cafe.dto.request.CreateCafeRequest;
import com.ssafy.yeobaek.cafe.dto.request.CreateDisplayWallRequest;
import com.ssafy.yeobaek.cafe.dto.response.*;
import com.ssafy.yeobaek.cafe.entity.Cafe;
import com.ssafy.yeobaek.cafe.entity.CafeCategory;
import com.ssafy.yeobaek.cafe.entity.CafeCategoryTag;
import com.ssafy.yeobaek.cafe.entity.DisplaySlot;
import com.ssafy.yeobaek.cafe.entity.DisplayWall;
import com.ssafy.yeobaek.cafe.mapper.CafeMapper;
import com.ssafy.yeobaek.cafe.repository.*;
import com.ssafy.yeobaek.common.code.ResponseCode;
import com.ssafy.yeobaek.common.exception.CustomException;
import com.ssafy.yeobaek.exhibition.entity.Exhibition;
import com.ssafy.yeobaek.exhibition.repository.ExhibitionRepository;
import com.ssafy.yeobaek.exhibition.service.ExhibitionService;
import com.ssafy.yeobaek.review.entity.Review;
import com.ssafy.yeobaek.review.repository.ReviewRepository;
import com.ssafy.yeobaek.review.service.ReviewService;
import com.ssafy.yeobaek.storage.service.S3Service;
import com.ssafy.yeobaek.user.dto.response.RatingStatsData;
import com.ssafy.yeobaek.user.entity.User;
import com.ssafy.yeobaek.user.service.UserService;
import com.ssafy.yeobaek.recommendation.service.EmbeddingService;
import com.ssafy.yeobaek.recommendation.service.RecommendationService;
import com.ssafy.yeobaek.recommendation.dto.response.CafeHybridResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CafeServiceImpl implements CafeService {

    private final CafeRepository cafeRepository;
    private final CafeCategoryTagRepository cafeCategoryTagRepository;
    private final CafeBookmarkRepository cafeBookmarkRepository;
    private final DisplayWallRepository displayWallRepository;
    private final DisplaySlotRepository displaySlotRepository;
    private final ExhibitionRepository exhibitionRepository;
    private final ReviewRepository reviewRepository;
    private final ArtworkRepository artworkRepository;
    private final UserService userService;
    private final S3Service s3Service;
    private final GeocoderService geocoderService;
    private final CafeCategoryService cafeCategoryService;
    private final CafeBookmarkService cafeBookmarkService;
    private final ExhibitionService exhibitionService;
    private final ReviewService reviewService;
    private final CafeMapper cafeMapper;
    private final EmbeddingService embeddingService;
    private final RecommendationService recommendationService;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    /**
     * 카페 등록
     * @param userId 유저 id
     * @param request 카페 등록 정보
     */
    @Override
    @Transactional
    public void createCafe(Long userId, CreateCafeRequest request) {
        log.info("카페 등록 시작 - 사용자 ID: {}", userId);
        // 1. 사용자 정보 확인
        User user = userService.findById(userId)
                .orElseThrow(() -> {
                    log.warn("카페 등록 실패 - 존재하지 않는 사용자 ID: {}", userId);
                    return new CustomException(ResponseCode.USER_NOT_FOUND);
                });

        // 2. 중복 등록 확인
        if (cafeRepository.existsByCafeOwner(user)) {
            log.warn("카페 등록 실패 - 이미 카페를 등록한 사용자: {}", userId);
            throw new CustomException(ResponseCode.CAFE_ALREADY_EXISTS);
        }

        // 3. 주소를 좌표로 변환
        Coordinates coordinates = geocoderService.getCoordinates(request.getAddress());
        log.info("주소 좌표 변환 완료 - 주소: {}, 좌표: ({}, {})", request.getAddress(), coordinates.getLatitude(), coordinates.getLongitude());

        // 4. 카페 엔티티 생성 및 저장
        Cafe cafe = cafeMapper.toCafeEntity(request, user, coordinates);
        Cafe savedCafe = cafeRepository.save(cafe);

        // 5. 카페 카테고리 저장
        List<CafeCategory> categories = cafeCategoryService.findCategoriesByIds(request.getCategoryIds());
        categories.forEach(cafeCategory -> {
            CafeCategoryTag cafeCategoryTag = CafeCategoryTag.builder()
                    .cafe(savedCafe)
                    .category(cafeCategory)
                    .build();
            cafeCategoryTagRepository.save(cafeCategoryTag);
        });
        log.info("카페 등록 완료 - 카페 ID: {}", savedCafe.getId());
    }

    /**
     * 내 카페 정보 수정
     * @param userId 유저 id
     * @param request 수정할 카페 정보
     */
    @Override
    @Transactional
    public void updateCafe(Long userId, UpdateCafeRequest request) {
        log.info("내 카페 정보 수정 시작 - 사용자 ID: {}", userId);
        // 1. 카페 조회
        Cafe cafe = cafeRepository.findByCafeOwnerId(userId)
                .orElseThrow(() -> {
                    log.warn("내 카페 정보 수정 실패 - 카페를 찾을 수 없음. 사용자 ID: {}", userId);
                    return new CustomException(ResponseCode.CAFE_NOT_FOUND);
                });
        log.info("수정할 카페를 찾았습니다 - 카페 ID: {}", cafe.getId());

        // 2. 필드별로 값이 존재하면 업데이트
        if (request.getCafeName() != null && !request.getCafeName().isBlank()) {
            cafe.setCafeName(request.getCafeName());
        }
        if (request.getDescription() != null) {
            cafe.setDescription(request.getDescription());
        }
        if (request.getContact() != null) {
            cafe.setContact(request.getContact());
        }
        if (request.getBusinessHoursStart() != null) {
            cafe.setBusinessStartTime(request.getBusinessHoursStart());
        }
        if (request.getBusinessHoursEnd() != null) {
            cafe.setBusinessEndTime(request.getBusinessHoursEnd());
        }
        if (request.getDailyVisitors() != null) {
            cafe.setDailyVisitors(request.getDailyVisitors());
        }

        // 3. 주소 변경 시 좌표 업데이트
        if (request.getAddress() != null && !request.getAddress().isBlank()) {
            Coordinates coordinates = geocoderService.getCoordinates(request.getAddress());
            cafe.setAddress(request.getAddress());
            cafe.setLatitude(coordinates.getLatitude());
            cafe.setLongitude(coordinates.getLongitude());
            log.info("주소 및 좌표 업데이트 완료");
        }

        // 4. 이미지 목록 변경
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            cafe.setImages(request.getImages());
            log.info("이미지 목록 업데이트 완료");
        }

        // 5. 카테고리 변경 (기존 연결 모두 삭제 후 새로 추가)
        if (request.getCategoryIds() != null) {
            log.info("카페 카테고리 업데이트 시작");
            cafeCategoryTagRepository.deleteAllByCafe(cafe);
            List<CafeCategory> categories = cafeCategoryService.findCategoriesByIds(request.getCategoryIds());
            categories.forEach(cafeCategory -> {
                CafeCategoryTag cafeCategoryTag = CafeCategoryTag.builder()
                        .cafe(cafe)
                        .category(cafeCategory)
                        .build();
                cafeCategoryTagRepository.save(cafeCategoryTag);
            });
            log.info("카페 카테고리 업데이트 완료 - {}개 카테고리 저장", categories.size());
        }

        // 6. 변경사항 저장
        cafeRepository.save(cafe);
        log.info("내 카페 정보 수정 완료 - 카페 ID: {}", cafe.getId());
    }

    /**
     * 카페 공간 등록
     * @param userId 유저 id
     * @param request 카페 공간 등록 정보
     */
    @Override
    @Transactional
    public List<Wall> createCafeWall(Long userId, List<CreateDisplayWallRequest> request) {
        log.info("카페 공간 등록 시작 - 사용자 ID: {}, 등록할 벽 개수: {}", userId, request.size());
        // 1. 사용자 정보 확인 및 카페 조회
        User user = userService.findById(userId)
                .orElseThrow(() -> {
                    log.warn("카페 공간 등록 실패 - 존재하지 않는 사용자 ID: {}", userId);
                    return new CustomException(ResponseCode.USER_NOT_FOUND);
                });
        Cafe cafe = cafeRepository.findByCafeOwner(user)
                .orElseThrow(() -> {
                    log.warn("카페 공간 등록 실패 - 카페를 찾을 수 없음. 사용자 ID: {}", userId);
                    return new CustomException(ResponseCode.CAFE_NOT_FOUND);
                });

        // 2. 중복 등록 확인
        if (displayWallRepository.existsByCafe(cafe)) {
            log.warn("카페 공간 등록 실패 - 이미 공간 정보가 등록된 카페: {}", cafe.getId());
            throw new CustomException(ResponseCode.WALL_ALREADY_EXISTS);
        }

        // 3. 요청된 벽 정보들을 순회하며 저장
        List<Wall> createdWalls = new ArrayList<>();
        request.forEach(wallRequest -> {
            DisplayWall displayWall = cafeMapper.toDisplayWallEntity(wallRequest, cafe);
            displayWallRepository.save(displayWall);

            // 4. 전시 가능한 벽인 경우, 슬롯 정보도 함께 저장
            if (wallRequest.getStatus()) {
                DisplaySlot displaySlot = DisplaySlot.builder()
                        .wall(displayWall)
                        .build();
                DisplaySlot savedSlot = displaySlotRepository.save(displaySlot);
                log.info("전시 가능 공간(슬롯) 생성 - 벽 ID: {}, 슬롯 ID: {}", displayWall.getId(), savedSlot.getId());

                // 비동기로 임베딩 생성 및 저장 (requirements가 있는 경우)
                if (savedSlot.getRequirements() != null && !savedSlot.getRequirements().trim().isEmpty()) {
                    embeddingService.generateAndSaveSlotEmbedding(savedSlot.getId());
                }
            }
            createdWalls.add(cafeMapper.toWallDto(displayWall));
        });
        log.info("카페 공간 등록 완료 - 사용자 ID: {}, 생성된 벽 개수: {}", userId, createdWalls.size());
        return createdWalls;
    }

    /**
     * 나의 카페 조회
     * @param userId 유저 id
     * @return 나의 카페 정보 및 공간 정보
     */
    @Override
    public MyCafeData getMyCafe(Long userId) {
        log.info("내 카페 정보 조회 시작 - 사용자 ID: {}", userId);
        // 1. 카페 조회
        Cafe cafe = cafeRepository.findByCafeOwnerId(userId)
                .orElseThrow(() -> {
                    log.warn("내 카페 정보 조회 실패 - 카페를 찾을 수 없음. 사용자 ID: {}", userId);
                    return new CustomException(ResponseCode.CAFE_NOT_FOUND);
                });

        // 2. 벽 + 슬롯 조회
        List<DisplayWall> walls = displayWallRepository.findAllWithSlotByCafeId(cafe.getId());

        // 3. 전시 정보 조회 및 맵 생성
        Map<Long, ExhibitionInfo> exhibitionInfoMap = walls.stream()
                .map(DisplayWall::getSlot)
                .filter(Objects::nonNull)
                .map(exhibitionService::findOngoingExhibitionBySlot)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toMap(
                        exhibition -> exhibition.getDisplaySlot().getId(),
                        this::createExhibitionInfo
                ));

        // 4. 카페 이미지 URL 변환
        String primaryImage = s3Service.generatePresignedUrlForThumbnail(cafe.getImages().get(0));

        // 5. 협업 작가 수 조회
        Long collaboratingArtists = exhibitionRepository.countDistinctArtistsByCafeId(cafe.getId());

        // 6. DTO 변환하여 반환
        MyCafeData myCafeData = cafeMapper.toMyCafeData(cafe, walls, primaryImage, exhibitionInfoMap, collaboratingArtists);
        log.info("내 카페 정보 조회 완료 - 카페 ID: {}", cafe.getId());
        return myCafeData;
    }


    /**
     * 카페 목록 조회 및 검색
     * @param isArtist 작가 여부
     * @param keyword 검색어
     * @param myLatitude 현재 위치 위도
     * @param myLongitude 현재 위치 경도
     * @param centerLatitude 지도 중심 위도
     * @param centerLongitude 지도 중심 경도
     * @param radiusKm 조회 반경
     * @param minWidth 최소 너비
     * @param maxWidth 최대 너비
     * @param minHeight 최소 높이
     * @param maxHeight 최대 높이
     * @param minCommission 최소 수수료
     * @param maxCommission 최대 수수료
     * @param page 페이지
     * @param size 사이즈
     * @return 카페 목록 데이터
     */
    @Override
    @Transactional(readOnly = true)
    public CafeListData searchCafes(Boolean isArtist, String keyword, Double myLatitude, Double myLongitude, Double centerLatitude, Double centerLongitude, Double radiusKm, Integer minWidth, Integer maxWidth, Integer minHeight, Integer maxHeight, Double minCommission, Double maxCommission, Integer page, Integer size) {
        log.info("카페 목록 검색 시작 - 키워드: '{}', 반경: {}km, 페이지: {}, 사이즈: {}", keyword, radiusKm, page, size);
        // 작가 전용 필터 권한 검사
        if (!isArtist) {
            if (minWidth != null || maxWidth != null || minHeight != null || maxHeight != null) {
                log.warn("일반 사용자가 작가 전용 필터(규격) 사용 시도");
                throw new CustomException(ResponseCode.SLOT_FILTER_FORBIDDEN);
            }
            if (minCommission != null || maxCommission != null) {
                log.warn("일반 사용자가 작가 전용 필터(수수료) 사용 시도");
                throw new CustomException(ResponseCode.CAFE_COMMISSION_FILTER_FORBIDDEN);
            }
        }

        // 1. 필터 조건 생성
        Specification<Cafe> spec = CafeSpecification.withFilters(keyword, centerLatitude, centerLongitude, radiusKm, minWidth, maxWidth, minHeight, maxHeight, minCommission, maxCommission);

        // 2. 페이지네이션 정보 생성
        Pageable pageable = PageRequest.of(page, size);

        // 3. 필터링 및 페이지네이션된 카페 조회
        Page<Cafe> cafePage = cafeRepository.findAll(spec, pageable);

        // 4. 조회된 카페들의 카테고리 태그를 한 번에 조회하여 맵으로 구성 (N+1 문제 방지)
        List<Long> cafeIds = cafePage.getContent().stream()
                .map(Cafe::getId)
                .collect(Collectors.toList());
        Map<Long, List<String>> cafeCategoriesMap = cafeCategoryTagRepository.findAllByCafe_IdIn(cafeIds).stream()
                .collect(Collectors.groupingBy(tag -> tag.getCafe().getId(),
                        Collectors.mapping(tag -> tag.getCategory().getCategoryName(), Collectors.toList())));

        // 5. DTO 변환
        List<CafeSummary> summaries = cafePage.getContent().stream()
                .map(cafe -> {
                    // 거리 계산 (사용자 위치 기준)
                    double distance = calculateDistance(myLatitude, myLongitude, cafe.getLatitude(), cafe.getLongitude());

                    // 카테고리 목록 추출
                    List<String> categories = cafeCategoriesMap.getOrDefault(cafe.getId(), Collections.emptyList());

                    // 대표 이미지 URL 추출
                    String primaryImageUrl = s3Service.generatePresignedUrlForThumbnail(cafe.getImages().get(0));

                    return cafeMapper.toCafeSummary(cafe, distance, categories, primaryImageUrl);
                })
                .collect(Collectors.toList());

        // 6. 페이지네이션 DTO 생성
        Pagination pagination = cafeMapper.toPagination(cafePage);

        log.info("카페 목록 검색 완료 - 조회된 카페 수: {}", summaries.size());
        return new CafeListData(summaries, pagination);
    }

    // Haversine 공식으로 두 지점 간의 거리를 계산 (km 단위)
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371; // 지구 반지름 (km)
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    /**
     * 카페 상세 정보 조회
     * @param userId 유저 id
     * @param cafeId 카페 id
     * @return 카페 상세 정보
     */
    @Override
    @Transactional
    public CafeDetailData getCafeDetail(Long userId, Long cafeId) {
        log.info("카페 상세 정보 조회 시작 - 사용자 ID: {}, 카페 ID: {}", userId, cafeId);
        // 1. Cafe 엔티티 조회
        Cafe cafe = cafeRepository.findById(cafeId)
            .orElseThrow(() -> {
                log.warn("카페 상세 정보 조회 실패 - 존재하지 않는 카페 ID: {}", cafeId);
                return new CustomException(ResponseCode.CAFE_NOT_FOUND);
            });

        // 2. 벽, 슬롯, 전시 정보 조회
        List<DisplayWall> walls = displayWallRepository.findAllWithSlotByCafeId(cafeId);

        // 3. 전시 정보 조회 및 맵 생성
        Map<Long, ExhibitionInfo> exhibitionInfoMap = walls.stream()
                .map(DisplayWall::getSlot)
                .filter(Objects::nonNull)
                .map(exhibitionService::findCurrentOrUpcomingExhibitionBySlot)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toMap(
                        exhibition -> exhibition.getDisplaySlot().getId(),
                        this::createExhibitionInfo
                ));

        // 4. 카테고리 정보 조회
        List<String> categories = cafeCategoryTagRepository.findAllByCafe(cafe).stream()
            .map(tag -> tag.getCategory().getCategoryName())
            .collect(Collectors.toList());

        // 5. 카페 이미지 URL 변환
        List<String> images = s3Service.generatePresignedUrlsForDownload(cafe.getImages());
        User ownerUser = cafe.getCafeOwner();
        String profileImage = s3Service.generatePresignedUrlForThumbnail(ownerUser.getProfileImageUrl());

        // 6. 북마크 여부 확인
        boolean isBookmarked = cafeBookmarkService.isBookmarked(userId, cafeId);

        // 7. 평점 정보 계산
        List<Review> reviews = reviewRepository.findAllByRevieweeId(ownerUser.getId());
        int totalReviews = reviews.size();
        double averageRating = reviews.isEmpty() ? 0.0 :
                reviews.stream()
                        .mapToInt(Review::getRating)
                        .average()
                        .orElse(0.0);
        RatingStatsData ratingStats = new RatingStatsData(totalReviews, averageRating);

        // 8. DTO 변환
        CafeDetailData cafeDetailData = cafeMapper.toCafeDetailData(cafe, walls, categories, images, profileImage, exhibitionInfoMap, isBookmarked, ratingStats);

        // 9. 조회수 1 증가
        cafe.increaseViewCount();
        cafeRepository.save(cafe);

        log.info("카페 상세 정보 조회 완료 - 카페 ID: {}", cafeId);
        return cafeDetailData;
    }

    /**
     * 슬롯 정보 수정
     * @param userId 유저 id
     * @param cafeId 카페 id
     * @param slotId 슬롯 id
     * @param request 슬롯 수정 정보
     * @return 슬롯 정보
     */
    @Override
    @Transactional
    public SlotData updateSlot(Long userId, Long cafeId, Long slotId, UpdateSlotRequest request) {
        log.info("슬롯 정보 수정 시작 - 사용자 ID: {}, 카페 ID: {}, 슬롯 ID: {}", userId, cafeId, slotId);
        // 1. 슬롯 조회
        DisplaySlot slot = displaySlotRepository.findById(slotId)
                .orElseThrow(() -> {
                    log.warn("슬롯 정보 수정 실패 - 존재하지 않는 슬롯 ID: {}", slotId);
                    return new CustomException(ResponseCode.SLOT_NOT_FOUND);
                });

        // 2. 슬롯이 속한 카페 확인
        Cafe cafe = slot.getWall().getCafe();
        if (!Objects.equals(cafeId, cafe.getId())) {
            log.warn("슬롯 정보 수정 실패 - 슬롯이 해당 카페에 속하지 않음. 슬롯 ID: {}, 카페 ID: {}", slotId, cafeId);
            throw new CustomException(ResponseCode.SLOT_NOT_FOUND_IN_CAFE);
        }

        // 3. 슬롯 소유 카페 확인 및 권한 검사
        if (!cafe.getCafeOwner().getId().equals(userId)) {
            log.warn("슬롯 정보 수정 실패 - 권한 없음. 사용자 ID: {}, 카페 소유자 ID: {}", userId, cafe.getCafeOwner().getId());
            throw new CustomException(ResponseCode.CAFE_FORBIDDEN_UPDATE_DELETE);
        }

        // 4. 슬롯 정보 업데이트
        boolean requirementsUpdated = false;
        if (request.getCommissionRate() != null) {
            slot.setCommissionRate(request.getCommissionRate());
        }
        if (request.getRequirements() != null) {
            slot.setRequirements(request.getRequirements());
            requirementsUpdated = true;
        }

        // 5. 수정된 정보 저장
        DisplaySlot savedSlot = displaySlotRepository.save(slot);

        // 6. requirements가 변경되었으면 임베딩 다시 생성
        if (requirementsUpdated && savedSlot.getRequirements() != null && !savedSlot.getRequirements().trim().isEmpty()) {
            log.info("슬롯의 requirements 변경으로 임베딩 재생성 - 슬롯 ID: {}", savedSlot.getId());
            // 기존 임베딩 초기화 후 비동기로 재생성
            savedSlot.setRequirementsEmbedding(null);
            displaySlotRepository.save(savedSlot);
            embeddingService.generateAndSaveSlotEmbedding(savedSlot.getId());
        }

        // 7. DTO 반환
        log.info("슬롯 정보 수정 완료 - 슬롯 ID: {}", savedSlot.getId());
        return cafeMapper.toSlotData(savedSlot);
    }

    private ExhibitionInfo createExhibitionInfo(Exhibition exhibition) {
        Artwork artwork = exhibition.getArtwork();
        String artworkImageUrl = artwork.getImages().isEmpty() ? null : s3Service.generatePresignedUrlForThumbnail(artwork.getImages().get(0));

        return new ExhibitionInfo(
                artwork.getId(),
                artwork.getTitle(),
                artworkImageUrl,
                artwork.getArtist().getUser().getNickname(),
                exhibition.getStartDate().format(DATE_FORMATTER),
                exhibition.getEndDate().format(DATE_FORMATTER)
        );
    }

    /**
     * 북마크한 카페 목록 조회
     * @param userId 사용자 ID
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 북마크한 카페 목록 데이터
     */
    @Override
    public CafeListData getBookmarkedCafes(Long userId, Integer page, Integer size) {
        log.info("북마크한 카페 목록 조회 시작 - 사용자 ID: {}, 페이지: {}, 사이즈: {}", userId, page, size);
        // 1. Pageable 객체 생성
        Pageable pageable = PageRequest.of(page, size);

        // 2. 사용자가 북마크한 카페 목록을 페이지네이션하여 조회
        Page<Cafe> cafePage = cafeBookmarkRepository.findBookmarkedCafesByUserId(userId, pageable);

        // 3. 조회된 카페들의 카테고리 태그를 한 번에 조회하여 맵으로 구성 (N+1 문제 방지)
        List<Long> cafeIds = cafePage.getContent().stream()
                .map(Cafe::getId)
                .collect(Collectors.toList());
        Map<Long, List<String>> cafeCategoriesMap = cafeCategoryTagRepository.findAllByCafe_IdIn(cafeIds).stream()
                .collect(Collectors.groupingBy(tag -> tag.getCafe().getId(),
                        Collectors.mapping(tag -> tag.getCategory().getCategoryName(), Collectors.toList())));

        // 4. DTO 변환
        List<CafeSummary> summaries = cafePage.getContent().stream()
                .map(cafe -> {
                    // 카페 카테고리
                    List<String> categories = cafeCategoriesMap.getOrDefault(cafe.getId(), Collections.emptyList());

                    // 카페 대표 이미지 URL 변환
                    String primaryImageUrl = s3Service.generatePresignedUrlForThumbnail(cafe.getImages().get(0));

                    // 북마크 목록에서는 거리를 계산하지 않으므로 null 전달
                    return cafeMapper.toCafeSummary(cafe, null, categories, primaryImageUrl);
                })
                .collect(Collectors.toList());

        // 5. 페이지네이션 DTO 생성
        Pagination pagination = cafeMapper.toPagination(cafePage);

        log.info("북마크한 카페 목록 조회 완료 - 조회된 카페 수: {}", summaries.size());
        return new CafeListData(summaries, pagination);
    }

    /**
     * 슬롯의 전시 가능일 조회
     * - 조회 시작일로부터 30일 동안의 가능한 날짜 목록 반환
     * - 승인됨(APPROVED) 또는 진행 중(ONGOING) 상태의 전시와 겹치지 않는 날짜만 반환
     * @param slotId 슬롯 ID
     * @param startDate 조회 시작일 (null이면 오늘 날짜 사용)
     * @return 전시 가능한 날짜 목록
     */
    @Override
    @Transactional(readOnly = true)
    public SlotAvailableDatesResponse getSlotAvailableDates(Long slotId, LocalDate startDate) {
        log.info("슬롯 전시 가능일 조회 시작 - 슬롯 ID: {}, 시작일: {}", slotId, startDate);
        // 1. 슬롯 존재 여부 확인
        if (!displaySlotRepository.existsById(slotId)) {
            log.warn("슬롯 전시 가능일 조회 실패 - 존재하지 않는 슬롯 ID: {}", slotId);
            throw new CustomException(ResponseCode.SLOT_NOT_FOUND);
        }

        // 2. 시작일 설정 및 검증 (null이면 오늘 날짜 사용, 오늘 이전이면 에러)
        LocalDate today = LocalDate.now();
        LocalDate queryStartDate = (startDate != null) ? startDate : today;
        if (queryStartDate.isBefore(today)) {
            log.warn("슬롯 전시 가능일 조회 실패 - 조회 시작일이 오늘보다 이전입니다: {}", queryStartDate);
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
        List<Exhibition> exhibitions = exhibitionRepository.findBySlotIdAndDateRange(slotId, queryStartDate, queryEndDate);

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
        SlotAvailableDatesResponse response = cafeMapper.toSlotAvailableDatesResponse(availableDates);
        log.info("슬롯 전시 가능일 조회 완료 - 슬롯 ID: {}, 가능한 날짜 수: {}", slotId, availableDates.size());
        return response;
    }

    /**
     * 카페의 전시 가능한 슬롯 목록 조회
     * - DisplaySlot이 존재하는 슬롯만 포함
     * - 지정된 기간에 승인됨(APPROVED) 또는 진행 중(ONGOING) 상태의 전시가 없는 슬롯만 반환
     * @param cafeId 카페 ID
     * @param startDate 조회 시작일
     * @param endDate 조회 종료일
     * @return 전시 가능한 슬롯 목록
     */
    @Override
    @Transactional(readOnly = true)
    public AvailableSlotsData getAvailableSlots(Long cafeId, LocalDate startDate, LocalDate endDate) {
        log.info("카페의 전시 가능 슬롯 목록 조회 시작 - 카페 ID: {}, 기간: {} ~ {}", cafeId, startDate, endDate);
        // 1. Cafe 존재 여부 확인
        if (!cafeRepository.existsById(cafeId)) {
            log.warn("카페의 전시 가능 슬롯 목록 조회 실패 - 존재하지 않는 카페 ID: {}", cafeId);
            throw new CustomException(ResponseCode.CAFE_NOT_FOUND);
        }

        // 2. 카페의 슬롯 목록 조회 (DisplaySlot이 존재하는 것만)
        List<DisplaySlot> displaySlots = displaySlotRepository.findAllByCafeIdAndDisplayable(cafeId);

        // 3. 각 슬롯에 대해 기간 내 전시 여부 확인 및 DTO 변환
        List<Slot> availableSlots = displaySlots.stream()
                .filter(slot -> {
                    // 해당 기간에 전시가 없는지 확인
                    List<Exhibition> exhibitions = exhibitionRepository.findBySlotIdAndDateRange(
                            slot.getId(), startDate, endDate
                    );
                    return exhibitions.isEmpty();
                })
                .map(cafeMapper::toAvailableSlotDto)
                .collect(Collectors.toList());

        // 4. 응답 DTO 변환 및 반환
        AvailableSlotsData response = cafeMapper.toAvailableSlotsData(availableSlots);
        log.info("카페의 전시 가능 슬롯 목록 조회 완료 - 카페 ID: {}, 가능한 슬롯 수: {}", cafeId, availableSlots.size());
        return response;
    }

    /**
     * 추천 카페 목록 조회 (하이브리드 추천: CF + CBF)
     * @param artworkId 작품 ID
     * @return 추천 카페 목록 (상위 10개)
     */
    @Override
    public CafeRecommendationResponse getRecommendedCafes(Long artworkId) {
        log.info("추천 카페 목록 조회 시작 - 작품 ID: {}", artworkId);
        // 1. 작품 존재 여부 확인
        if (!artworkRepository.existsById(artworkId)) {
            log.warn("추천 카페 목록 조회 실패 - 존재하지 않는 작품 ID: {}", artworkId);
            throw new CustomException(ResponseCode.ARTWORK_NOT_FOUND);
        }

        try {
            // 상위 10개 추천
            int topN = 10;

            // 파이썬 추천 서버로부터 추천 카페 ID 목록 받기 (CF + CBF 하이브리드)
            CafeHybridResponse recommendationResponse = recommendationService.recommendCafesForArtwork(artworkId, topN);

            // 추천된 카페 ID 리스트 추출
            List<Long> recommendedCafeIds = recommendationResponse.getRecommendations().stream()
                    .map(CafeHybridResponse.CafeItem::getCafeId)
                    .toList();
            log.info("추천 서버로부터 카페 추천 응답 받음 - 추천된 카페 수: {}", recommendedCafeIds.size());

            // DB에서 카페 조회 (추천 순서 유지)
            List<Cafe> cafes = recommendedCafeIds.stream()
                    .map(id -> cafeRepository.findById(id).orElse(null))
                    .filter(Objects::nonNull)
                    .toList();

            // 카페 ID 목록 수집
            List<Long> cafeIds = cafes.stream()
                    .map(Cafe::getId)
                    .collect(Collectors.toList());

            if (cafeIds.isEmpty()) {
                log.info("추천된 카페가 없어 빈 목록 반환");
                // 카페가 없으면 빈 목록 반환
                return cafeMapper.toCafeRecommendationResponse(new ArrayList<>(), null);
            }

            // 3. 카테고리 일괄 조회
            Map<Long, List<String>> cafeCategoriesMap = cafeCategoryTagRepository.findAllByCafe_IdIn(cafeIds).stream()
                .collect(Collectors.groupingBy(
                        tag -> tag.getCafe().getId(),
                        Collectors.mapping(tag -> tag.getCategory().getCategoryName(), Collectors.toList())
                ));

            // 4. 카페 오너 ID 일괄 수집
            List<Long> ownerIds = cafes.stream()
                    .map(cafe -> cafe.getCafeOwner().getId())
                    .collect(Collectors.toList());

            // 5. 리뷰 일괄 조회 (평점 계산용)
            List<Review> reviews = reviewRepository.findAllByRevieweeIdIn(ownerIds);
            Map<Long, List<Review>> reviewsByOwnerId = reviews.stream()
                    .collect(Collectors.groupingBy(review -> review.getReviewee().getId()));

            // 6. 슬롯 일괄 조회 (전시 가능한 슬롯만)
            List<DisplaySlot> displaySlots = displaySlotRepository.findAllByCafeIdInAndDisplayable(cafeIds);
            Map<Long, List<DisplaySlot>> slotsByCafeId = displaySlots.stream()
                    .collect(Collectors.groupingBy(slot -> slot.getWall().getCafe().getId()));

            // 7. 각 카페별 DTO 변환
            List<CafeSummaryForRecommendation> cafeSummaries = cafes.stream()
                .map(cafe -> {
                    // 대표 이미지 URL 변환 (첫 번째 이미지 사용)
                    String primaryImageUrl = null;
                    if (cafe.getImages() != null && !cafe.getImages().isEmpty()) {
                        primaryImageUrl = s3Service.generatePresignedUrlForThumbnail(cafe.getImages().get(0));
                    }

                    // 카테고리 조회
                    List<String> categories = cafeCategoriesMap.getOrDefault(cafe.getId(), new ArrayList<>());

                    // 평점 계산 (카페 오너의 리뷰 평균)
                    Long ownerId = cafe.getCafeOwner().getId();
                    List<Review> ownerReviews = reviewsByOwnerId.getOrDefault(ownerId, new ArrayList<>());
                    Double totalReviewScore = ownerReviews.stream()
                            .mapToInt(Review::getRating)
                            .average()
                            .orElse(0.0);
                    // 소수점 첫째 자리까지 반올림
                    totalReviewScore = Math.round(totalReviewScore * 10.0) / 10.0;

                    // 슬롯 목록 변환
                    List<DisplaySlot> cafeSlots = slotsByCafeId.getOrDefault(cafe.getId(), new ArrayList<>());
                    List<Slot> slots = cafeSlots.stream()
                            .map(cafeMapper::toSlotForRecommendation)
                            .collect(Collectors.toList());

                    // DTO 변환
                    return cafeMapper.toCafeSummaryForRecommendation(
                            cafe, primaryImageUrl, categories, totalReviewScore, slots
                    );
                    })
                    .collect(Collectors.toList());

            // Response 반환 (pagination은 null)
            log.info("추천 카페 목록 조회 완료 - 작품 ID: {}, 반환된 카페 수: {}", artworkId, cafeSummaries.size());
            return cafeMapper.toCafeRecommendationResponse(cafeSummaries, null);

        } catch (Exception e) {
            log.error("추천 서버로부터 카페 추천 목록을 가져오는데 실패했습니다 - 작품 ID: {}", artworkId, e);
            log.info("인기순으로 폴백 추천을 진행합니다.");
            return getFallbackRecommendations();
        }
    }

    /**
     * 추천 실패 시 폴백 - 인기 카페 목록 반환
     */
    private CafeRecommendationResponse getFallbackRecommendations() {
        // 인기도 기준 상위 10개 카페 조회
        Pageable pageable = PageRequest.of(0, 10, Sort.by(
                Sort.Order.desc("bookmarkCount"),
                Sort.Order.desc("viewCount")
        ));
        Page<Cafe> cafePage = cafeRepository.findAll(pageable);
        log.info("폴백 추천 - 인기 카페 {}개를 조회했습니다.", cafePage.getNumberOfElements());
        return buildCafeRecommendationFromPage(cafePage);
    }

    private CafeRecommendationResponse buildCafeRecommendationFromPage(Page<Cafe> cafePage) {
        List<Long> cafeIds = cafePage.getContent().stream()
                .map(Cafe::getId)
                .collect(Collectors.toList());

        if (cafeIds.isEmpty()) {
            Pagination pagination = cafeMapper.toPagination(cafePage);
            return cafeMapper.toCafeRecommendationResponse(new ArrayList<>(), pagination);
        }

        Map<Long, List<String>> cafeCategoriesMap = cafeCategoryTagRepository.findAllByCafe_IdIn(cafeIds).stream()
                .collect(Collectors.groupingBy(
                        tag -> tag.getCafe().getId(),
                        Collectors.mapping(tag -> tag.getCategory().getCategoryName(), Collectors.toList())
                ));

        List<Long> ownerIds = cafePage.getContent().stream()
                .map(cafe -> cafe.getCafeOwner().getId())
                .collect(Collectors.toList());

        List<Review> reviews = reviewRepository.findAllByRevieweeIdIn(ownerIds);
        Map<Long, List<Review>> reviewsByOwnerId = reviews.stream()
                .collect(Collectors.groupingBy(review -> review.getReviewee().getId()));

        List<DisplaySlot> displaySlots = displaySlotRepository.findAllByCafeIdInAndDisplayable(cafeIds);
        Map<Long, List<DisplaySlot>> slotsByCafeId = displaySlots.stream()
                .collect(Collectors.groupingBy(slot -> slot.getWall().getCafe().getId()));

        List<CafeSummaryForRecommendation> cafeSummaries = cafePage.getContent().stream()
                .map(cafe -> {
                    String primaryImageUrl = null;
                    if (cafe.getImages() != null && !cafe.getImages().isEmpty()) {
                        primaryImageUrl = s3Service.generatePresignedUrlForThumbnail(cafe.getImages().get(0));
                    }

                    List<String> categories = cafeCategoriesMap.getOrDefault(cafe.getId(), new ArrayList<>());

                    Long ownerId = cafe.getCafeOwner().getId();
                    List<Review> ownerReviews = reviewsByOwnerId.getOrDefault(ownerId, new ArrayList<>());
                    Double totalReviewScore = ownerReviews.stream()
                            .mapToInt(Review::getRating)
                            .average()
                            .orElse(0.0);
                    totalReviewScore = Math.round(totalReviewScore * 10.0) / 10.0;

                    List<DisplaySlot> cafeSlots = slotsByCafeId.getOrDefault(cafe.getId(), new ArrayList<>());
                    List<Slot> slots = cafeSlots.stream()
                            .map(cafeMapper::toSlotForRecommendation)
                            .collect(Collectors.toList());

                    return cafeMapper.toCafeSummaryForRecommendation(
                            cafe, primaryImageUrl, categories, totalReviewScore, slots
                    );
                })
                .collect(Collectors.toList());

        Pagination pagination = cafeMapper.toPagination(cafePage);
        return cafeMapper.toCafeRecommendationResponse(cafeSummaries, pagination);
    }

    /**
     * 인기 카페 목록 조회
     * - 북마크 수와 조회수를 9:1로 가중치를 두어 상위 N개 카페를 조회
     * @param size 조회할 작품 수 (최대 20개)
     * @return 인기 카페 목록
     */
    @Override
    public PopularCafesData getPopularCafes(Integer size) {
        log.info("인기 카페 목록 조회 시작 - 사이즈: {}", size);
        // 1. 상위 인기 카페 조회
        Pageable pageable = PageRequest.of(0, size);
        List<Cafe> popularCafes = cafeRepository.findPopularCafes(pageable);

        if (popularCafes.isEmpty()) {
            log.info("인기 카페가 없습니다.");
            return new PopularCafesData(Collections.emptyList());
        }

        // 2. 카테고리 정보 일괄 조회 (N+1 방지)
        List<Long> cafeIds = popularCafes.stream().map(Cafe::getId).collect(Collectors.toList());
        Map<Long, List<String>> cafeCategoriesMap = cafeCategoryTagRepository.findAllByCafe_IdIn(cafeIds).stream()
                .collect(Collectors.groupingBy(
                        tag -> tag.getCafe().getId(),
                        Collectors.mapping(tag -> tag.getCategory().getCategoryName(), Collectors.toList())
                ));

        // 3. DTO 변환
        List<CafeSummary> summaries = popularCafes.stream()
                .map(cafe -> {
                    List<String> categories = cafeCategoriesMap.getOrDefault(cafe.getId(), Collections.emptyList());
                    String primaryImageUrl = s3Service.generatePresignedUrlForThumbnail(cafe.getImages().get(0));
                    // 인기 카페 목록에서는 거리를 계산하지 않으므로 null 전달
                    return cafeMapper.toCafeSummary(cafe, null, categories, primaryImageUrl);
                })
                .collect(Collectors.toList());

        log.info("인기 카페 목록 조회 완료 - 조회된 카페 수: {}", summaries.size());
        return new PopularCafesData(summaries);
    }
}
