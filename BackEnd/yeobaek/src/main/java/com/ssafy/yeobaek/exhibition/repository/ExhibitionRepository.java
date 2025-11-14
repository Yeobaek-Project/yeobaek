package com.ssafy.yeobaek.exhibition.repository;

import com.ssafy.yeobaek.cafe.entity.DisplaySlot;
import com.ssafy.yeobaek.common.enums.ExhibitionState;
import com.ssafy.yeobaek.exhibition.entity.Exhibition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExhibitionRepository extends JpaRepository<Exhibition, Long> {

    /**
     * 특정 슬롯의 특정 기간에 중복 전시가 있는지 확인
     * @param slotId 슬롯 ID
     * @param startDate 전시 시작일
     * @param endDate 전시 종료일
     * @return 중복 여부
     */
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END " +
           "FROM Exhibition e " +
           "WHERE e.displaySlot.id = :slotId " +
           "AND e.state IN ('APPROVED', 'ONGOING') " +
           "AND ((e.startDate <= :endDate AND e.endDate >= :startDate))")
    boolean existsConflictingSlot(
            @Param("slotId") Long slotId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /**
     * 특정 작품의 특정 기간에 중복 전시가 있는지 확인
     * @param artworkId 작품 ID
     * @param startDate 전시 시작일
     * @param endDate 전시 종료일
     * @return 중복 여부
     */
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END " +
           "FROM Exhibition e " +
           "WHERE e.artwork.id = :artworkId " +
           "AND e.state IN ('APPROVED', 'ONGOING') " +
           "AND ((e.startDate <= :endDate AND e.endDate >= :startDate))")
    boolean existsConflictingArtwork(
            @Param("artworkId") Long artworkId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /**
     * 특정 작품의 오늘 날짜 기준 진행 중인 전시 조회
     * @param artworkId 작품 ID
     * @param today 오늘 날짜
     * @return 진행 중인 전시
     */
    @Query("SELECT e FROM Exhibition e " +
           "WHERE e.artwork.id = :artworkId " +
           "AND e.state = 'ONGOING' " +
           "AND e.startDate <= :today " +
           "AND e.endDate >= :today")
    Optional<Exhibition> findCurrentExhibitionByArtworkId(
            @Param("artworkId") Long artworkId,
            @Param("today") LocalDate today
    );

    /**
     * 특정 슬롯에 대해 현재 진행 중이거나 예정된 전시 중 가장 빠른 전시를 조회
     * @param displaySlot 슬롯 엔티티
     * @param states 전시 상태 목록 (ONGOING, APPROVED)
     * @return 조건에 맞는 첫 번째 전시
     */
    Optional<Exhibition> findFirstByDisplaySlotAndStateInOrderByStartDateAsc(DisplaySlot displaySlot, List<ExhibitionState> states);

    /**
     * 특정 작가의 오늘 날짜 기준 현재 전시 중인 작품 수 조회
     * @param artistId 작가 ID
     * @param today 오늘 날짜
     * @return 현재 전시 중인 작품 수
     */
    @Query("SELECT COUNT(e) FROM Exhibition e " +
           "WHERE e.artwork.artist.id = :artistId " +
           "AND e.state = 'ONGOING' " +
           "AND e.startDate <= :today " +
           "AND e.endDate >= :today")
    Integer countCurrentExhibitionsByArtistId(
            @Param("artistId") Long artistId,
            @Param("today") LocalDate today
    );

    /**
     * 특정 작품의 특정 기간 내 전시 목록 조회 (승인됨 또는 진행 중인 전시만)
     * @param artworkId 작품 ID
     * @param startDate 조회 시작일
     * @param endDate 조회 종료일
     * @return 해당 기간과 겹치는 전시 목록
     */
    @Query("SELECT e FROM Exhibition e " +
           "WHERE e.artwork.id = :artworkId " +
           "AND e.state IN ('APPROVED', 'ONGOING') " +
           "AND ((e.startDate <= :endDate AND e.endDate >= :startDate))")
    List<Exhibition> findByArtworkIdAndDateRange(
            @Param("artworkId") Long artworkId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /**
     * 특정 작가의 총 전시 완료 횟수 조회
     * @param artistId 작가 ID
     * @return 완료된 전시 수
     */
    @Query("SELECT COUNT(e) FROM Exhibition e " +
           "WHERE e.artwork.artist.id = :artistId " +
           "AND e.state = 'COMPLETED'")
    Integer countCompletedExhibitionsByArtistId(@Param("artistId") Long artistId);
    
    /**
     * 특정 슬롯의 특정 기간 내 전시 목록 조회 (승인됨 또는 진행 중인 전시만)
     * @param slotId 슬롯 ID
     * @param startDate 조회 시작일
     * @param endDate 조회 종료일
     * @return 해당 기간과 겹치는 전시 목록
     */
    @Query("SELECT e FROM Exhibition e " +
            "WHERE e.displaySlot.id = :slotId " +
            "AND e.state IN ('APPROVED', 'ONGOING') " +
            "AND ((e.startDate <= :endDate AND e.endDate >= :startDate))")
    List<Exhibition> findBySlotIdAndDateRange(
            @Param("slotId") Long slotId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /**
     * 특정 슬롯의 특정 날짜 이후 전시 목록 조회 (승인됨 또는 진행 중인 전시만)
     * @param slotId 슬롯 ID
     * @param fromDate 조회 기준일 (해당 날짜에 진행 중이거나 그 이후 전시)
     * @return 조건에 맞는 전시 목록 (시작일 기준 오름차순)
     */
    @Query("SELECT e FROM Exhibition e " +
            "WHERE e.displaySlot.id = :slotId " +
            "AND e.state IN ('APPROVED', 'ONGOING') " +
            "AND e.endDate >= :fromDate " +
            "ORDER BY e.startDate ASC")
    List<Exhibition> findBySlotIdAndFromDate(
            @Param("slotId") Long slotId,
            @Param("fromDate") LocalDate fromDate
    );

    /**
     * 특정 상태와 시작일을 기준으로 전시 목록 조회
     * @param state 전시 상태
     * @param startDate 전시 시작일
     * @return 전시 목록
     */
    List<Exhibition> findAllByStateAndStartDate(ExhibitionState state, LocalDate startDate);

    /**
     * 특정 상태와 종료일을 기준으로 전시 목록 조회
     * @param state 전시 상태
     * @param endDate 전시 종료일
     * @return 전시 목록
     */
    List<Exhibition> findAllByStateAndEndDate(ExhibitionState state, LocalDate endDate);

    /**
     * 특정 카페와 협업한 작가 수 조회 (완료된 전시 기준)
     * @param cafeId 카페 ID
     * @return 협업한 작가 수
     */
    @Query("SELECT COUNT(DISTINCT e.artwork.artist.id) " +
            "FROM Exhibition e " +
            "WHERE e.displaySlot.wall.cafe.id = :cafeId " +
            "AND e.state = 'COMPLETED'")
    Long countDistinctArtistsByCafeId(@Param("cafeId") Long cafeId);
}
