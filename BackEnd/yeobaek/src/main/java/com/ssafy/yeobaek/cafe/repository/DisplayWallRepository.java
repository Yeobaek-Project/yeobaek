package com.ssafy.yeobaek.cafe.repository;

import com.ssafy.yeobaek.cafe.entity.Cafe;
import com.ssafy.yeobaek.cafe.entity.DisplayWall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DisplayWallRepository extends JpaRepository<DisplayWall, Long> {
    boolean existsByCafe(Cafe cafe);

    // 특정 카페의 모든 전시 벽 + 슬롯 함께 조회 (fetch join)
    @Query("SELECT w FROM DisplayWall w " +
            "LEFT JOIN FETCH w.slot " +
            "WHERE w.cafe.id = :cafeId")
    List<DisplayWall> findAllWithSlotByCafeId(@Param("cafeId") Long cafeId);
}
