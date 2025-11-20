package com.ssafy.yeobaek.artwork.mapper;

import com.ssafy.yeobaek.artwork.dto.response.*;
import com.ssafy.yeobaek.artwork.entity.Artwork;
import com.ssafy.yeobaek.common.dto.Pagination;
import com.ssafy.yeobaek.exhibition.entity.Exhibition;
import com.ssafy.yeobaek.user.entity.Artist;
import com.ssafy.yeobaek.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * Artwork 도메인 엔티티와 DTO 간의 변환을 담당하는 매퍼
 */
@Component
public class ArtworkMapper {

    /**
     * Artwork 엔티티를 ArtworkDetail DTO로 변환
     * @param artwork Artwork 엔티티
     * @param isBookmarked 현재 사용자의 북마크 여부
     * @param artworkImageUrls 변환된 작품 이미지 URL 리스트 (presigned URL)
     * @param artistProfileImageUrl 변환된 작가 프로필 이미지 URL (presigned URL)
     * @param exhibitionDto 현재 전시 정보 (없으면 null)
     * @return ArtworkDetail DTO
     */
    public ArtworkDetailDto toArtworkDetail(Artwork artwork, boolean isBookmarked, List<String> artworkImageUrls, String artistProfileImageUrl, ExhibitionDto exhibitionDto) {
        ArtistDto artistDto = toArtist(artwork.getArtist(), artistProfileImageUrl);

        return ArtworkDetailDto.builder()
                .artworkId(artwork.getId())
                .title(artwork.getTitle())
                .description(artwork.getDescription())
                .price(artwork.getPrice())
                .category(artwork.getArtCategory())
                .widthCm(artwork.getWidthCm().intValue())
                .heightCm(artwork.getHeightCm().intValue())
                .weightKg(artwork.getWeightKg())
                .productionYear(artwork.getProductionYear())
                .isExhibiting(artwork.getIsExhibiting())
                .viewCount(artwork.getViewCount())
                .bookmarkCount(artwork.getBookmarkCount())
                .isBookmarked(isBookmarked)
                .images(artworkImageUrls)
                .artist(artistDto)
                .currentExhibition(exhibitionDto)
                .build();
    }

    /**
     * Artwork 엔티티를 ArtworkDetailData로 변환
     * @param artwork Artwork 엔티티
     * @param isBookmarked 현재 사용자의 북마크 여부
     * @param artworkImageUrls 변환된 작품 이미지 URL 리스트 (presigned URL)
     * @param artistProfileImageUrl 변환된 작가 프로필 이미지 URL (presigned URL)
     * @param exhibitionDto 현재 전시 정보 (없으면 null)
     * @return ArtworkDetailData
     */
    public ArtworkDetailResponse toArtworkDetailData(Artwork artwork, boolean isBookmarked, List<String> artworkImageUrls, String artistProfileImageUrl, ExhibitionDto exhibitionDto) {
        ArtworkDetailDto artworkDetailDto = toArtworkDetail(artwork, isBookmarked, artworkImageUrls, artistProfileImageUrl, exhibitionDto);
        return ArtworkDetailResponse.builder()
                .artwork(artworkDetailDto)
                .build();
    }

    /**
     * Artist 엔티티를 Artist DTO로 변환
     * @param artist Artist 엔티티
     * @param profileImageUrl 변환된 프로필 이미지 URL (presigned URL)
     * @return Artist DTO
     */
    public ArtistDto toArtist(Artist artist, String profileImageUrl) {
        return ArtistDto.builder()
                .userId(artist.getUser().getId())
                .artistName(artist.getUser().getNickname())
                .profileImageUrl(profileImageUrl)
                .build();
    }

    /**
     * Exhibition 엔티티를 Exhibition DTO로 변환
     * @param exhibition Exhibition 엔티티
     * @param cafeImageUrl 카페 이미지 URL (presigned URL)
     * @return Exhibition DTO
     */
    public ExhibitionDto toExhibition(Exhibition exhibition, String cafeImageUrl) {
        return ExhibitionDto.builder()
                .cafeId(exhibition.getCafe().getId())
                .cafeName(exhibition.getCafe().getCafeName())
                .cafeAddress(exhibition.getCafe().getAddress())
                .cafeImageUrl(cafeImageUrl)
                .exhibitionStartDate(exhibition.getStartDate())
                .exhibitionEndDate(exhibition.getEndDate())
                .build();
    }

    /**
     * Artwork 엔티티를 ArtworkSummary DTO로 변환 (기본 정보만)
     * @param artwork Artwork 엔티티
     * @param thumbnailUrl 썸네일 이미지 URL (presigned URL)
     * @return ArtworkSummary DTO
     */
    public ArtworkSummaryDto toArtworkSummary(Artwork artwork, String thumbnailUrl) {
        return ArtworkSummaryDto.builder()
                .artworkId(artwork.getId())
                .title(artwork.getTitle())
                .thumbnailUrl(thumbnailUrl)
                .build();
    }

    /**
     * Artwork 엔티티를 ArtworkSummary DTO로 변환 (확장된 정보 포함)
     * @param artwork Artwork 엔티티
     * @param thumbnailUrl 썸네일 이미지 URL (presigned URL)
     * @return ArtworkSummary DTO (모든 필드 포함)
     */
    public ArtworkSummaryDto toArtworkSummaryWithDetails(Artwork artwork, String thumbnailUrl) {
        return ArtworkSummaryDto.builder()
                .artworkId(artwork.getId())
                .title(artwork.getTitle())
                .thumbnailUrl(thumbnailUrl)
                .price(artwork.getPrice())
                .category(artwork.getArtCategory().name())
                .widthCm(artwork.getWidthCm().intValue())
                .heightCm(artwork.getHeightCm().intValue())
                .weightKg(artwork.getWeightKg())
                .productionYear(artwork.getProductionYear())
                .isExhibiting(artwork.getIsExhibiting())
                .bookmarkCount(artwork.getBookmarkCount())
                .build();
    }

    /**
     * Artist 엔티티를 Artist DTO로 변환 (검색 결과용 - introduction 포함)
     * @param artist Artist 엔티티
     * @param artworkCount 작품 수
     * @param profileImageUrl 변환된 프로필 이미지 URL (presigned URL)
     * @return Artist DTO
     */
    public ArtistDto toArtist(Artist artist, int artworkCount, String profileImageUrl) {
        User user = artist.getUser();
        return ArtistDto.builder()
                .userId(user.getId())
                .artistName(user.getNickname())
                .profileImageUrl(profileImageUrl)
                .introduction(artist.getIntroduction())
                .artworkCount(artworkCount)
                .build();
    }

    /**
     * ArtworkListData DTO 생성
     * @param artists 작가 목록
     * @param artworks 작품 목록
     * @param pagination 페이징 정보
     * @return ArtworkListData DTO
     */
    public ArtworkListResponse toArtworkListData(List<ArtistDto> artists, List<ArtworkSummaryDto> artworks, Pagination pagination) {
        return ArtworkListResponse.builder()
                .artists(artists)
                .artworks(artworks)
                .pagination(pagination)
                .build();
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
     * Artist 엔티티를 ArtistDto로 변환 (작가 홈용 - 전체 통계 포함)
     * @param artist Artist 엔티티
     * @param profileImageUrl 변환된 프로필 이미지 URL (presigned URL)
     * @param artworkCount 전체 작품 수
     * @param currentExhibitionCount 현재 전시 중인 작품 수
     * @return ArtistDto
     */
    public ArtistDto toArtistDtoForArtistHome(Artist artist, String profileImageUrl, Integer artworkCount, Integer currentExhibitionCount) {
        User user = artist.getUser();
        return ArtistDto.builder()
                .userId(user.getId())
                .artistName(user.getNickname())
                .profileImageUrl(profileImageUrl)
                .artworkCount(artworkCount)
                .currentExhibitionCount(currentExhibitionCount)
                .build();
    }

    /**
     * Artwork 엔티티를 ArtworkSummaryDto로 변환 (작가 홈용 - 작가 정보 및 카페명 포함)
     * @param artwork Artwork 엔티티
     * @param thumbnailUrl 썸네일 이미지 URL (presigned URL)
     * @param cafeName 전시 장소(카페명)
     * @return ArtworkSummaryDto
     */
    public ArtworkSummaryDto toArtworkSummaryDtoForArtistHome(Artwork artwork, String thumbnailUrl, String cafeName) {
        return ArtworkSummaryDto.builder()
                .artworkId(artwork.getId())
                .title(artwork.getTitle())
                .thumbnailUrl(thumbnailUrl)
                .price(artwork.getPrice())
                .category(artwork.getArtCategory().name())
                .cafeName(cafeName)
                .isExhibiting(artwork.getIsExhibiting())
                .bookmarkCount(artwork.getBookmarkCount())
                .build();
    }

    /**
     * artist와 artworks를 ArtistHomeResponse로 변환
     * @param artist 작가
     * @param artworks 인기 작품 목록(3개)
     * @return ArtistHomeResponse
     */
    public ArtistHomeResponse toArtistHomeResponse(ArtistDto artist, List<ArtworkSummaryDto> artworks) {
        return ArtistHomeResponse.builder()
                .artist(artist)
                .artworks(artworks)
                .build();
    }

    /**
     * Artwork 엔티티를 ArtworkSummaryDto로 변환 (북마크 목록용 - 작가 정보 및 카페명 포함)
     * @param artwork Artwork 엔티티
     * @param thumbnailUrl 썸네일 이미지 URL (presigned URL)
     * @param cafeName 전시 장소(카페명)
     * @param artistDto 작가 정보
     * @return ArtworkSummaryDto
     */
    public ArtworkSummaryDto toArtworkSummaryDtoForBookmark(Artwork artwork, String thumbnailUrl, String cafeName, ArtistDto artistDto) {
        return ArtworkSummaryDto.builder()
                .artworkId(artwork.getId())
                .title(artwork.getTitle())
                .thumbnailUrl(thumbnailUrl)
                .price(artwork.getPrice())
                .category(artwork.getArtCategory().name())
                .cafeName(cafeName)
                .isExhibiting(artwork.getIsExhibiting())
                .bookmarkCount(artwork.getBookmarkCount())
                .artist(artistDto)
                .build();
    }

    /**
     * 전시 가능한 날짜 목록을 ArtworkAvailableDatesResponse로 변환
     * @param availableDates 전시 가능한 날짜 목록
     * @return ArtworkAvailableDatesResponse
     */
    public ArtworkAvailableDatesResponse toArtworkAvailableDatesResponse(List<LocalDate> availableDates) {
        return ArtworkAvailableDatesResponse.builder()
                .dates(availableDates)
                .build();
    }

    /**
     * Artwork 엔티티를 ArtworkSummaryDto로 변환 (전시 가능한 작품 목록용 - API 명세에 맞춘 필드만 포함)
     * @param artwork Artwork 엔티티
     * @param thumbnailUrl 썸네일 이미지 URL (presigned URL)
     * @return ArtworkSummaryDto (artworkId, title, thumbnailUrl, widthCm, heightCm, weightKg만 포함)
     */
    public ArtworkSummaryDto toArtworkSummaryForAvailableArtworks(Artwork artwork, String thumbnailUrl) {
        return ArtworkSummaryDto.builder()
                .artworkId(artwork.getId())
                .title(artwork.getTitle())
                .thumbnailUrl(thumbnailUrl)
                .widthCm(artwork.getWidthCm().intValue())
                .heightCm(artwork.getHeightCm().intValue())
                .weightKg(artwork.getWeightKg())
                .build();
    }
}
