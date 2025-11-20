package com.ssafy.yeobaek.cafe.mapper;

import com.ssafy.yeobaek.cafe.dto.Vec2;
import com.ssafy.yeobaek.cafe.dto.request.CreateCafeRequest;
import com.ssafy.yeobaek.cafe.dto.request.CreateDisplayWallRequest;
import com.ssafy.yeobaek.cafe.dto.response.*;
import com.ssafy.yeobaek.cafe.entity.Cafe;
import com.ssafy.yeobaek.cafe.entity.DisplaySlot;
import com.ssafy.yeobaek.cafe.entity.DisplayWall;
import com.ssafy.yeobaek.common.dto.Pagination;
import com.ssafy.yeobaek.user.dto.response.RatingStatsData;
import com.ssafy.yeobaek.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class CafeMapper {

    /**
     * 카페 정보 DTO를 Cafe 엔티티로 변환
     * @param request 카페 정보
     * @param owner 카페 사장
     * @param coordinates 카페 위치 좌표
     * @return Cafe 엔티티
     */
    public Cafe toCafeEntity(CreateCafeRequest request, User owner, Coordinates coordinates) {
        return Cafe.builder()
                .cafeOwner(owner)
                .cafeName(request.getCafeName())
                .address(request.getAddress())
                .latitude(coordinates.getLatitude())
                .longitude(coordinates.getLongitude())
                .description(request.getDescription())
                .contact(request.getContact())
                .businessStartTime(request.getBusinessHoursStart())
                .businessEndTime(request.getBusinessHoursEnd())
                .dailyVisitors(request.getDailyVisitors())
                .images(request.getImages())
                .build();
    }

    /**
     * 카페 벽 DTO를 DisplayWall 엔티티로 변환
     * @param request 카페 벽 정보
     * @param cafe 카페
     * @return DisplayWall 엔티티
     */
    public DisplayWall toDisplayWallEntity(CreateDisplayWallRequest request, Cafe cafe) {
        return DisplayWall.builder()
                .cafe(cafe)
                .wallName(request.getWallName())
                .startX(request.getStart().getX())
                .startY(request.getStart().getY())
                .endX(request.getEnd().getX())
                .endY(request.getEnd().getY())
                .widthCm(request.getWidth())
                .heightCm(request.getHeight())
                .status(request.getStatus())
                .build();
    }

    /**
     * DisplayWall 엔티티를 벽 DTO로 변환
     * @param displayWall 카페 벽 정보
     * @return Wall DTO
     */
    public Wall toWallDto(DisplayWall displayWall) {
        return new Wall(
                displayWall.getId(),
                displayWall.getWallName(),
                new Vec2(displayWall.getStartX(), displayWall.getStartY()),
                new Vec2(displayWall.getEndX(), displayWall.getEndY()),
                displayWall.getStatus()
        );
    }

    /**
     * Cafe 엔티티와 DisplayWall 엔티티 리스트를 내 카페 정보 DTO로 변환
     * @param cafe 카페 정보
     * @param displayWalls 카페 공간 정보
     * @param primaryImage 카페 대표 이미지
     * @param exhibitionInfoMap 전시 정보
     * @param collaboratingArtists 협업 작가 수
     * @return MyCafeData DTO
     */
    public MyCafeData toMyCafeData(Cafe cafe, List<DisplayWall> displayWalls, String primaryImage, Map<Long, ExhibitionInfo> exhibitionInfoMap, Long collaboratingArtists) {

        // 통계 계산
        long totalSlots = displayWalls.size();
        long exhibitingSlots = displayWalls.stream()
                .filter(w -> w.getSlot() != null && w.getSlot().getIsExhibiting())
                .count();

        // 슬롯 정보
        List<Slot> slots = displayWalls.stream()
                .map(wall -> toSlotDto(wall, wall.getStatus() ? exhibitionInfoMap.get(wall.getSlot().getId()) : null))
                .collect(Collectors.toList());

        // 내 카페 정보
        MyCafe myCafe = new MyCafe(
                cafe.getId(),
                cafe.getCafeName(),
                cafe.getAddress(),
                primaryImage,
                totalSlots,
                exhibitingSlots,
                collaboratingArtists,
                slots
        );

        return new MyCafeData(myCafe);
    }

    /**
     * Cafe 엔티티를 CafeSummary DTO로 변환
     * @param cafe 카페 엔티티
     * @param distance 사용자 위치로부터의 거리
     * @param categories 카페 카테고리 이름 리스트
     * @return CafeSummary DTO
     */
    public CafeSummary toCafeSummary(Cafe cafe, Double distance, List<String> categories, String primaryImageUrl) {
        int totalSlotCount = cafe.getDisplayWalls().size();
        int availableSlotCount = (int) cafe.getDisplayWalls().stream().filter(DisplayWall::getStatus).count();

        return new CafeSummary(
                cafe.getId(),
                cafe.getCafeName(),
                cafe.getAddress(),
                cafe.getLatitude(),
                cafe.getLongitude(),
                primaryImageUrl,
                availableSlotCount,
                totalSlotCount,
                distance,
                categories
        );
    }

    /**
     * Cafe 엔티티와 관련 정보들을 카페 상세 정보 DTO로 변환
     * @param cafe 카페 정보
     * @param displayWalls 카페 공간 정보
     * @param categories 카페 카테고리 목록
     * @return CafeDetailData DTO
     */
    public CafeDetailData toCafeDetailData(Cafe cafe, List<DisplayWall> displayWalls, List<String> categories, List<String> images, String profileImage, Map<Long, ExhibitionInfo> exhibitionInfoMap, Boolean isBookmarked, RatingStatsData ratingStats) {

        // 1. 슬롯 정보
        List<Slot> slots = displayWalls.stream()
                .map(wall -> toSlotDto(wall, wall.getStatus() ? exhibitionInfoMap.get(wall.getSlot().getId()) : null))
                .toList();

        // 2. 카페 사장 정보 DTO 생성
        User ownerUser = cafe.getCafeOwner();
        CafeOwner owner = new CafeOwner(ownerUser.getId(), ownerUser.getNickname(), profileImage, ratingStats);

        // 3. 최종 CafeDetail DTO 조립
        CafeDetail cafeDetail = new CafeDetail(
                cafe.getId(),
                cafe.getCafeName(),
                cafe.getAddress(),
                cafe.getDescription(),
                cafe.getContact(),
                cafe.getBusinessStartTime(),
                cafe.getBusinessEndTime(),
                cafe.getDailyVisitors(),
                cafe.getViewCount(),
                isBookmarked,
                categories,
                images,
                slots,
                owner
        );

        return new CafeDetailData(cafeDetail);
    }

    /**
     * DisplayWall 엔티티를 슬롯 DTO로 변환
     * @param displayWall 벽 정보
     * @return Slot DTO
     */
    public Slot toSlotDto(DisplayWall displayWall, ExhibitionInfo exhibitionInfo){

        DisplaySlot displaySlot = displayWall.getSlot();

        return Slot.builder()
                .wallId(displayWall.getId())
                .wallName(displayWall.getWallName())
                .start(new Vec2(displayWall.getStartX(), displayWall.getStartY()))
                .end(new Vec2(displayWall.getEndX(), displayWall.getEndY()))
                .width(displayWall.getWidthCm())
                .height(displayWall.getHeightCm())
                .isDisplayable(displayWall.getStatus())
                .isExhibiting(displaySlot != null ? displaySlot.getIsExhibiting() : null)
                .commissionRate(displaySlot != null ? displaySlot.getCommissionRate() : null)
                .requirements(displaySlot != null ? displaySlot.getRequirements() : null)
                .currentExhibition(exhibitionInfo)
                .build();
    }

    /**
     * DisplaySlot 엔티티를 DTO로 변환
     * @param displaySlot 슬롯 정보
     * @return SlotData DTO
     */
    public SlotData toSlotData(DisplaySlot displaySlot){

        Slot slot = Slot.builder()
                .wallId(displaySlot.getId())
                .wallName(displaySlot.getWall().getWallName())
                .width(displaySlot.getWall().getWidthCm())
                .height(displaySlot.getWall().getHeightCm())
                .isDisplayable(true)
                .isExhibiting(displaySlot.getIsExhibiting())
                .commissionRate(displaySlot.getCommissionRate())
                .requirements(displaySlot.getRequirements())
                .build();

        return new SlotData(slot);
    }

    /**
     * 전시 가능한 날짜 목록을 SlotAvailableDatesResponse로 변환
     * @param availableDates 전시 가능한 날짜 목록
     * @return SlotAvailableDatesResponse
     */
    public SlotAvailableDatesResponse toSlotAvailableDatesResponse(List<LocalDate> availableDates) {
        return SlotAvailableDatesResponse.builder()
                .dates(availableDates)
                .build();
    }

    /**
     * DisplaySlot 엔티티를 전시 가능한 슬롯 정보 DTO로 변환 (간소화 버전)
     * @param displaySlot 슬롯 정보
     * @return Slot DTO (필요한 필드만 포함)
     */
    public Slot toAvailableSlotDto(DisplaySlot displaySlot) {
        return Slot.builder()
                .wallId(displaySlot.getId())
                .wallName(displaySlot.getWall().getWallName())
                .width(displaySlot.getWall().getWidthCm())
                .height(displaySlot.getWall().getHeightCm())
                .commissionRate(displaySlot.getCommissionRate())
                .requirements(displaySlot.getRequirements())
                .build();
    }

    /**
     * Slot 목록을 AvailableSlotsData로 변환
     * @param slots 전시 가능한 슬롯 목록
     * @return AvailableSlotsData DTO
     */
    public AvailableSlotsData toAvailableSlotsData(List<Slot> slots) {
        return new AvailableSlotsData(slots);
    }

    /**
     * Spring Data Page 객체를 Pagination DTO로 변환
     * @param page Spring Data Page 객체
     * @return Pagination DTO
     */
    public Pagination toPagination(Page<?> page) {
        return Pagination.builder()
                .currentPage(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .hasNext(page.hasNext())
                .build();
    }

    /**
     * DisplaySlot을 추천용 Slot DTO로 변환 (slotName, isExhibiting만)
     * @param displaySlot 슬롯 정보
     * @return Slot DTO (wallName, isExhibiting만 포함, 나머지는 null)
     */
    public Slot toSlotForRecommendation(DisplaySlot displaySlot) {
        return Slot.builder()
                .wallName(displaySlot.getWall().getWallName())
                .isExhibiting(displaySlot.getIsExhibiting())
                .build();
    }

    /**
     * Cafe 엔티티를 추천용 CafeSummary DTO로 변환
     * @param cafe Cafe 엔티티
     * @param primaryImageUrl 대표 이미지 URL (presigned URL)
     * @param categories 인테리어 스타일 목록
     * @param totalReviewScore 총 평점
     * @param slots 전시 슬롯 목록
     * @return CafeSummaryForRecommendation DTO
     */
    public CafeSummaryForRecommendation toCafeSummaryForRecommendation(
            Cafe cafe,
            String primaryImageUrl,
            List<String> categories,
            Double totalReviewScore,
            List<Slot> slots
    ) {
        return CafeSummaryForRecommendation.builder()
                .cafeId(cafe.getId())
                .cafeName(cafe.getCafeName())
                .address(cafe.getAddress())
                .primaryImageUrl(primaryImageUrl)
                .categories(categories)
                .totalReviewScore(totalReviewScore)
                .slots(slots)
                .build();
    }

    /**
     * 추천 카페 목록과 페이징 정보를 응답 DTO로 변환
     * @param cafes 추천 카페 목록
     * @param pagination 페이징 정보
     * @return CafeRecommendationResponse
     */
    public CafeRecommendationResponse toCafeRecommendationResponse(
            List<CafeSummaryForRecommendation> cafes,
            Pagination pagination
    ) {
        return CafeRecommendationResponse.builder()
                .cafes(cafes)
                .pagination(pagination)
                .build();
    }
}