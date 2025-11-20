package com.ssafy.yeobaek.artwork.repository;

import com.ssafy.yeobaek.artwork.entity.Artwork;
import com.ssafy.yeobaek.user.entity.Artist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArtworkRepository extends JpaRepository<Artwork, Long>, JpaSpecificationExecutor<Artwork> {

    /**
     * 작가별 작품 수 조회
     * @param artist 작가 엔티티
     * @return 작품 수
     */
    int countByArtist(Artist artist);

    /**
     * 작가별 작품 목록 조회 (페이징)
     * @param artist 작가 엔티티
     * @param pageable 페이징 정보
     * @return 작품 목록 (페이징)
     */
    Page<Artwork> findByArtist(Artist artist, Pageable pageable);

    /**
     * 작가별 인기 작품 조회 (조회수 1 + 북마크수 9 가중치)
     * @param artistId 작가 ID
     * @param pageable 페이징 정보 (상위 N개 조회용)
     * @return 인기순 작품 목록
     */
    @Query("SELECT a FROM Artwork a " +
           "WHERE a.artist.id = :artistId " +
           "ORDER BY (a.viewCount * 1 + a.bookmarkCount * 9) DESC")
    List<Artwork> findTopPopularByArtistId(@Param("artistId") Long artistId, Pageable pageable);

    /**
     * 전체 작품 중 인기 작품 조회 (조회수 1 + 북마크수 9 가중치)
     * @param pageable 페이징 정보 (상위 N개 조회용)
     * @return 인기순 작품 목록
     */
    @Query("SELECT a FROM Artwork a " +
           "JOIN FETCH a.artist art " +
           "JOIN FETCH art.user " +
           "ORDER BY (a.viewCount * 1 + a.bookmarkCount * 9) DESC")
    List<Artwork> findTopPopularArtworks(Pageable pageable);

    /**
     * 전체 작품 중 인기 작품 조회 (조회수 1 + 북마크수 9 가중치) - 페이징
     * @param pageable 페이징 정보
     * @return 인기순 작품 목록 (페이징)
     */
    @Query("SELECT a FROM Artwork a ORDER BY (a.viewCount * 1 + a.bookmarkCount * 9) DESC")
    Page<Artwork> findTopPopularArtworksPage(Pageable pageable);

    /**
     * 작가별 전시 중인 작품 수 조회
     * @param artist 작가 엔티티
     * @return 전시 중인 작품 수
     */
    int countByArtistAndIsExhibiting(Artist artist, Boolean isExhibiting);

    /**
     * 특정 작가의 모든 작품 조회
     * @param artistId 작가 ID
     * @return 작품 목록
     */
    List<Artwork> findAllByArtistId(@Param("artistId") Long artistId);
}
