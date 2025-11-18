package com.ssafy.yeobaek.cafe.service;

import com.ssafy.yeobaek.cafe.dto.request.CreateCafeRequest;

import com.ssafy.yeobaek.cafe.dto.request.CreateDisplayWallRequest;
import com.ssafy.yeobaek.cafe.dto.request.UpdateCafeRequest;
import com.ssafy.yeobaek.cafe.dto.request.UpdateSlotRequest;
import com.ssafy.yeobaek.cafe.dto.response.*;

import java.time.LocalDate;
import java.util.List;

public interface CafeService {

    /**
     * 카페 등록
     * @param userId 유저 id
     * @param request 카페 등록 정보
     */
    void createCafe(Long userId, CreateCafeRequest request);

    /**
     * 내 카페 정보 수정
     * @param userId 유저 id
     * @param request 수정할 카페 정보
     */
    void updateCafe(Long userId, UpdateCafeRequest request);

    /**
     * 카페 공간 등록
     * @param userId 유저 id
     * @param request 카페 공간 등록 정보
     */
    List<Wall> createCafeWall(Long userId, List<CreateDisplayWallRequest> request);

    /**
     * 나의 카페 조회
     * @param userId 유저 id
     * @return 나의 카페 정보 및 공간 정보
     */
    MyCafeData getMyCafe(Long userId);

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
    CafeListData searchCafes(Boolean isArtist, String keyword, Double myLatitude, Double myLongitude, Double centerLatitude, Double centerLongitude, Double radiusKm, Integer minWidth, Integer maxWidth,
        Integer minHeight, Integer maxHeight, Double minCommission, Double maxCommission, Integer page, Integer size);

    /**
     * 카페 상세 정보 조회
     * @param userId 유저 id
     * @param cafeId 카페 id
     * @return 카페 상세 정보
     */
    CafeDetailData getCafeDetail(Long userId, Long cafeId);

    /**
     * 슬롯 정보 수정
     * @param userId 유저 id
     * @param cafeId 카페 id
     * @param slotId 슬롯 id
     * @param request 슬롯 수정 정보
     * @return 슬롯 정보
     */
    SlotData updateSlot(Long userId, Long cafeId, Long slotId, UpdateSlotRequest request);

    /**
     * 북마크한 카페 목록 조회
     * - 북마크 생성일 기준 내림차순 정렬
     * @param userId 사용자 ID
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 북마크한 작품 목록 및 페이징 정보
     */
    CafeListData getBookmarkedCafes(Long userId, Integer page, Integer size);

    /**
     * 슬롯의 전시 가능일 조회
     * - 조회 시작일로부터 30일 동안의 가능한 날짜 목록 반환
     * - 승인됨(APPROVED) 또는 진행 중(ONGOING) 상태의 전시와 겹치지 않는 날짜만 반환
     * @param slotId 슬롯 ID
     * @param startDate 조회 시작일 (null이면 오늘 날짜 사용)
     * @return 전시 가능한 날짜 목록
     */
    SlotAvailableDatesResponse getSlotAvailableDates(Long slotId, LocalDate startDate);

    /**
     * 카페의 전시 가능한 슬롯 목록 조회
     * - DisplaySlot이 존재하는 슬롯만 포함
     * - 지정된 기간에 승인됨(APPROVED) 또는 진행 중(ONGOING) 상태의 전시가 없는 슬롯만 반환
     * @param cafeId 카페 ID
     * @param startDate 조회 시작일
     * @param endDate 조회 종료일
     * @return 전시 가능한 슬롯 목록
     */
    AvailableSlotsData getAvailableSlots(Long cafeId, LocalDate startDate, LocalDate endDate);

    /**
     * 추천 카페 목록 조회 (하이브리드 추천: CF + CBF)
     * @param artworkId 작품 ID
     * @return 추천 카페 목록 (상위 10개)
     */
    CafeRecommendationResponse getRecommendedCafes(Long artworkId);

    /**
     * 인기 카페 목록 조회
     * - 북마크 수와 조회수를 9:1로 가중치를 두어 상위 N개 카페를 조회
     * @param size 조회할 작품 수 (최대 20개)
     * @return 인기 카페 목록
     */
    PopularCafesData getPopularCafes(Integer size);
}
