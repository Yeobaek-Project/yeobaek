package com.ssafy.yeobaek.artwork.service;

import com.ssafy.yeobaek.artwork.dto.request.CreateArtworkRequest;
import com.ssafy.yeobaek.artwork.dto.request.UpdateArtworkRequest;
import com.ssafy.yeobaek.artwork.dto.response.ArtistHomeResponse;
import com.ssafy.yeobaek.artwork.dto.response.ArtworkAvailableDatesResponse;
import com.ssafy.yeobaek.artwork.dto.response.ArtworkDetailResponse;
import com.ssafy.yeobaek.artwork.dto.response.ArtworkListResponse;
import com.ssafy.yeobaek.common.enums.ArtCategory;
import com.ssafy.yeobaek.common.enums.ArtworkSortBy;

import java.time.LocalDate;
import java.util.List;

public interface ArtworkService {

    /**
     * 작품 등록
     * @param userId 사용자 ID
     * @param request 작품 등록 정보
     */
    void createArtwork(Long userId, CreateArtworkRequest request);

    /**
     * 작품 수정
     * @param userId 사용자 ID (권한 검증용)
     * @param artworkId 수정할 작품 ID
     * @param request 작품 수정 정보
     */
    void updateArtwork(Long userId, Long artworkId, UpdateArtworkRequest request);

    /**
     * 작품 상세 조회
     * @param artworkId 작품 ID
     * @param userId 현재 사용자 ID (북마크 여부 확인용)
     * @return 작품 상세 정보
     */
    ArtworkDetailResponse getArtworkDetail(Long artworkId, Long userId);

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
    ArtworkListResponse searchArtworks(
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
    );

    /**
     * 특정 사용자의 작품 목록 조회
     * @param userId 조회할 사용자 ID
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 작품 목록 및 페이징 정보
     */
    ArtworkListResponse getUserArtworks(Long userId, Integer page, Integer size);

    /**
     * 작가 홈 조회 (인기 작품 상위 3개 + 작가 프로필)
     * - 인기도 기준: 조회수 × 1 + 북마크수 × 9
     * - 전시 중인 작품은 카페명 포함
     * - 모든 작품에 작가 프로필 정보 포함
     * @param userId 작가 ID
     * @return 인기 작품 목록 (최대 3개)
     */
    ArtistHomeResponse getArtistHome(Long userId);

    /**
     * 북마크한 작품 목록 조회
     * - 북마크 생성일 기준 내림차순 정렬
     * - 각 작품에 작가 정보 포함
     * - 전시 중인 작품은 카페명 포함
     * @param userId 사용자 ID
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 북마크한 작품 목록 및 페이징 정보
     */
    ArtworkListResponse getBookmarkedArtworks(Long userId, Integer page, Integer size);

    /**
     * 인기 작품 목록 조회
     * - 인기도 기준: 조회수 × 1 + 북마크수 × 9
     * - 각 작품에 작가 정보 포함
     * - 전시 중인 작품은 카페명 포함
     * @param size 조회할 작품 수 (최대 20개)
     * @return 인기 작품 목록 (pagination은 null)
     */
    ArtworkListResponse getPopularArtworks(Integer size);

    /**
     * 추천 작품 목록 조회 (하이브리드 추천: CF + CBF)
     * @param slotId 슬롯 ID
     * @return 추천 작품 목록 (상위 10개)
     */
    ArtworkListResponse getRecommendedArtworks(Long slotId);

    /**
     * 작품의 전시 가능일 조회
     * - 조회 시작일로부터 30일 동안의 가능한 날짜 목록 반환
     * - 승인됨(APPROVED) 또는 진행 중(ONGOING) 상태의 전시와 겹치지 않는 날짜만 반환
     * @param artworkId 작품 ID
     * @param startDate 조회 시작일 (null이면 오늘 날짜 사용)
     * @return 전시 가능한 날짜 목록
     */
    ArtworkAvailableDatesResponse getArtworkAvailableDates(Long artworkId, LocalDate startDate);

    /**
     * 전시 가능한 작품 목록 조회
     * - 특정 작가의 특정 기간에 전시 가능한 작품 목록 반환
     * - 요청된 기간에 전시가 없는 작품만 반환 (APPROVED 또는 ONGOING 상태의 전시와 겹치지 않음)
     * @param artistId 작가 ID
     * @param startDate 조회 시작일
     * @param endDate 조회 종료일
     * @return 전시 가능한 작품 목록
     */
    ArtworkListResponse getAvailableArtworks(Long artistId, LocalDate startDate, LocalDate endDate);
}
