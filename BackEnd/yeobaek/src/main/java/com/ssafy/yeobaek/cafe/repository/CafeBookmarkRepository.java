package com.ssafy.yeobaek.cafe.repository;

import com.ssafy.yeobaek.cafe.entity.Cafe;
import com.ssafy.yeobaek.cafe.entity.CafeBookmark;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CafeBookmarkRepository extends JpaRepository<CafeBookmark, Long> {

    /**
     * 특정 사용자가 특정 카페를 북마크했는지 확인
     * @param userId 사용자 ID
     * @param cafeId 카페 ID
     * @return 북마크 여부
     */
    boolean existsByUser_IdAndCafe_Id(Long userId, Long cafeId);

    /**
     * 사용자가 특정 카페를 북마크한 엔티티 삭제
     * @param userId 사용자 ID
     * @param cafeId 카페 ID
     */
    void deleteCafeBookmarkByUser_IdAndCafe_Id(Long userId, Long cafeId);

    /**
     * 특정 사용자가 북마크한 카페 목록을 페이지네이션하여 조회
     * @param userId 사용자 ID
     * @param pageable 페이지 정보
     * @return 북마크된 카페 페이지
     */
    @Query("SELECT cb.cafe " +
            "FROM CafeBookmark cb " +
            "WHERE cb.user.id = :userId " +
            "ORDER BY cb.createdAt DESC")
    Page<Cafe> findBookmarkedCafesByUserId(@Param("userId") Long userId, Pageable pageable);

    /**
     * 특정 사용자의 카페 북마크 수 조회
     * @param userId 사용자 ID
     * @return 북마크 수
     */
    long countByUser_Id(Long userId);
}
