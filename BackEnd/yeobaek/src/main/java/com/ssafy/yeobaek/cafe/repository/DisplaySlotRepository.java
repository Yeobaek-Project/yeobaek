package com.ssafy.yeobaek.cafe.repository;

import com.ssafy.yeobaek.cafe.entity.DisplaySlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DisplaySlotRepository extends JpaRepository<DisplaySlot, Long> {

    /**
     * 카페의 전시 가능한 슬롯 목록 조회
     * @param cafeId 카페 ID
     * @return DisplaySlot 목록
     */
    @Query("SELECT ds FROM DisplaySlot ds " +
           "JOIN FETCH ds.wall w " +
           "WHERE w.cafe.id = :cafeId " +
           "AND w.status = true")
    List<DisplaySlot> findAllByCafeIdAndDisplayable(@Param("cafeId") Long cafeId);

    /**
     * 여러 카페의 전시 가능한 슬롯 목록 일괄 조회
     * @param cafeIds 카페 ID 목록
     * @return DisplaySlot 목록
     */
    @Query("SELECT ds FROM DisplaySlot ds " +
           "JOIN FETCH ds.wall w " +
           "WHERE w.cafe.id IN :cafeIds " +
           "AND w.status = true")
    List<DisplaySlot> findAllByCafeIdInAndDisplayable(@Param("cafeIds") List<Long> cafeIds);
}
