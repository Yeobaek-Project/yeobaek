package com.ssafy.yeobaek.exhibition.service;

import com.ssafy.yeobaek.cafe.entity.DisplaySlot;
import com.ssafy.yeobaek.exhibition.dto.request.CreateExhibitionRequest;
import com.ssafy.yeobaek.exhibition.dto.response.ExhibitionSummaryResponse;
import com.ssafy.yeobaek.exhibition.entity.Exhibition;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExhibitionService {

    /**
     * 전시 제안 생성
     * - 카페 사장 또는 작가가 전시를 제안
     * - 제안자는 자동으로 동의 상태가 되며, 상대방이 동의하면 전시가 확정
     * - 전시 기간 검증 및 슬롯/작품 중복 체크 수행
     *
     * @param userId 현재 사용자 ID (제안자)
     * @param request 전시 제안 요청 정보
     */
    void createExhibitionProposal(Long userId, CreateExhibitionRequest request);

    /**
     * 전시 제안 동의
     * - PENDING 상태의 전시만 동의 가능
     * - 카페 사장: 본인 카페의 전시인지 확인 후 동의
     * - 작가: 본인 작품의 전시인지 확인 후 동의
     * - 양쪽 모두 동의 시 자동으로 APPROVED 상태로 변경
     *
     * @param userId 현재 사용자 ID
     * @param exhibitionId 전시 ID
     */
    void agreeExhibition(Long userId, Long exhibitionId);

    /**
     * 전시 제안 거절
     * - PENDING 상태의 전시만 거절 가능
     * - 카페 사장 또는 작가만 거절 가능
     * - 거절 시 전시가 REJECTED 상태로 변경
     *
     * @param userId 현재 사용자 ID
     * @param exhibitionId 전시 ID
     */
    void rejectExhibition(Long userId, Long exhibitionId);

    /**
     * 특정 작품의 현재 진행 중인 전시 조회
     * - 오늘 날짜 기준으로 진행 중인 전시 반환
     * - 전시가 없으면 Optional.empty() 반환
     *
     * @param artworkId 작품 ID
     * @return 현재 진행 중인 전시
     */
    Optional<Exhibition> getCurrentExhibitionByArtwork(Long artworkId);

    /**
     * 슬롯의 현재 또는 예정된 전시 조회
     * @param displaySlot 조회할 슬롯
     * @return 현재 진행 중이거나 예정된 전시 중 가장 빠른 전시
     */
    Optional<Exhibition> findCurrentOrUpcomingExhibitionBySlot(DisplaySlot displaySlot);

    /**
     * 슬롯의 현재 진행 중인 전시 조회
     * @param displaySlot 조회할 슬롯
     * @return 현재 진행 중인 전시
     */
    Optional<Exhibition> findOngoingExhibitionBySlot(DisplaySlot displaySlot);

    /**
     * 특정 작가의 현재 전시 중인 작품 수 조회
     * - 오늘 날짜 기준으로 전시 중인 작품 수 반환
     *
     * @param artistId 작가 ID
     * @return 현재 전시 중인 작품 수
     */
    Integer getCurrentExhibitionCountByArtist(Long artistId);

    /**
     * 전시 ID로 전시 요약 정보 조회
     * - 전시의 기본 정보를 반환
     * - 카페, 작품, 슬롯의 간략한 정보 포함
     *
     * @param exhibitionId 전시 ID
     * @return 전시 요약 정보
     */
    ExhibitionSummaryResponse getExhibitionSummary(Long exhibitionId);

    /**
     * 슬롯별 전시 일정 목록 조회
     * - 특정 날짜 이후의 전시 일정 목록을 반환
     * - APPROVED 또는 ONGOING 상태인 전시만 조회
     * - 시작일 기준 오름차순 정렬
     *
     * @param slotId 슬롯 ID
     * @param from 조회 기준 날짜
     * @return 전시 일정 목록
     */
    List<ExhibitionSummaryResponse> getExhibitionsBySlot(Long slotId, LocalDate from);
}
