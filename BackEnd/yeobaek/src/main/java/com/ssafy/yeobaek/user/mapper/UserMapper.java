package com.ssafy.yeobaek.user.mapper;

import com.ssafy.yeobaek.user.dto.response.ArtistData;
import com.ssafy.yeobaek.user.dto.response.CafeData;
import com.ssafy.yeobaek.user.dto.response.RatingStatsData;
import com.ssafy.yeobaek.user.dto.response.UserData;
import com.ssafy.yeobaek.user.entity.Artist;
import com.ssafy.yeobaek.user.entity.User;
import org.springframework.stereotype.Component;

/**
 * User 도메인 엔티티와 DTO 간의 변환을 담당하는 매퍼
 */
@Component
public class UserMapper {

    /**
     * User 엔티티를 UserData DTO로 변환
     * @param user User 엔티티
     * @param profileImageUrl 변환된 프로필 이미지 URL (presigned URL)
     * @param bookmarkCount 북마크 수
     * @return UserData DTO
     */
    public UserData toUserData(User user, String profileImageUrl, int bookmarkCount) {
        return UserData.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .userType(user.getUserType())
                .profileImageUrl(profileImageUrl)
                .birthDate(user.getBirthDate().toString())
                .gender(user.getGender())
                .bookmarkCount(bookmarkCount)
                .build();
    }

    /**
     * User 엔티티를 공개 프로필용 UserData DTO로 변환
     * 공개되는 정보: userId, nickname, userType, profileImageUrl만 포함
     * @param user User 엔티티
     * @param profileImageUrl 변환된 프로필 이미지 URL (presigned URL)
     * @return 공개 프로필용 UserData DTO
     */
    public UserData toPublicUserData(User user, String profileImageUrl) {
        return UserData.builder()
                .userId(user.getId())
                .email(null)
                .nickname(user.getNickname())
                .userType(user.getUserType())
                .profileImageUrl(profileImageUrl)
                .birthDate(null)
                .gender(null)
                .bookmarkCount(null)
                .build();
    }

    /**
     * Artist 엔티티를 ArtistData DTO로 변환
     * @param artist Artist 엔티티
     * @param artworkCount 작품 수
     * @param exhibitingCount 전시 중인 작품 수
     * @param totalExhibitedCount 총 전시한 작품 수
     * @param totalReviews 총 리뷰 수
     * @param averageRating 평균 평점
     * @return ArtistData DTO
     */
    public ArtistData toArtistData(
            Artist artist,
            int artworkCount,
            int exhibitingCount,
            int totalExhibitedCount,
            int totalReviews,
            double averageRating
    ) {
        RatingStatsData ratingStats = RatingStatsData.builder()
                .totalReviews(totalReviews)
                .averageRating(averageRating)
                .build();

        return ArtistData.builder()
                .introduction(artist.getIntroduction())
                .category(artist.getCategory())
                .artworkCount(artworkCount)
                .exhibitingCount(exhibitingCount)
                .totalExhibitedCount(totalExhibitedCount)
                .ratingStats(ratingStats)
                .build();
    }

    /**
     * Cafe 엔티티를 CafeData DTO로 변환
     * @param cafe Cafe 엔티티
     * @param totalReviews 총 리뷰 수
     * @param averageRating 평균 평점
     * @return CafeData DTO
     */
    public CafeData toCafeData(
            com.ssafy.yeobaek.cafe.entity.Cafe cafe,
            int totalReviews,
            double averageRating
    ) {
        RatingStatsData ratingStats = RatingStatsData.builder()
                .totalReviews(totalReviews)
                .averageRating(averageRating)
                .build();

        return CafeData.builder()
                .cafeId(cafe.getId())
                .cafeName(cafe.getCafeName())
                .address(cafe.getAddress())
                .description(cafe.getDescription())
                .contact(cafe.getContact())
                .businessHoursStart(cafe.getBusinessStartTime())
                .businessHoursEnd(cafe.getBusinessEndTime())
                .ratingStats(ratingStats)
                .build();
    }
}
