package com.ssafy.yeobaek.artwork.repository;

import com.ssafy.yeobaek.artwork.entity.ArtworkBookmark;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtworkBookmarkRepository extends JpaRepository<ArtworkBookmark, Long> {

    /**
     * 특정 사용자가 특정 작품을 북마크했는지 확인
     * @param userId 사용자 ID
     * @param artworkId 작품 ID
     * @return 북마크 여부
     */
    boolean existsByUser_IdAndArtwork_Id(Long userId, Long artworkId);

    /**
     * 사용자가 특정 작품을 북마크한 엔티티 삭제
     * @param userId 사용자 ID
     * @param artworkId 작품 ID
     */
    void deleteArtworkBookmarkByUser_IdAndArtwork_Id(Long userId, Long artworkId);

    /**
     * 사용자가 북마크한 작품 목록 조회 (페이징)
     * @param userId 사용자 ID
     * @param pageable 페이징 정보
     * @return 북마크한 작품 목록
     */
    @Query("SELECT ab FROM ArtworkBookmark ab " +
           "JOIN FETCH ab.artwork a " +
           "JOIN FETCH a.artist art " +
           "JOIN FETCH art.user " +
           "WHERE ab.user.id = :userId " +
           "ORDER BY ab.createdAt DESC")
    Page<ArtworkBookmark> findByUser_IdOrderByCreatedAtDesc(@Param("userId") Long userId, Pageable pageable);

    /**
     * 특정 사용자의 작품 북마크 수 조회
     * @param userId 사용자 ID
     * @return 북마크 수
     */
    long countByUser_Id(Long userId);
}
