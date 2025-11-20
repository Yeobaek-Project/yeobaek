package com.ssafy.yeobaek.artwork.mapper;

import com.ssafy.yeobaek.artwork.dto.response.BookmarkToggleResponse;
import com.ssafy.yeobaek.artwork.entity.Artwork;
import com.ssafy.yeobaek.artwork.entity.ArtworkBookmark;
import com.ssafy.yeobaek.user.entity.User;
import org.springframework.stereotype.Component;

/**
 * ArtworkBookmark 도메인 엔티티와 DTO 간의 변환을 담당하는 매퍼
 */
@Component
public class ArtworkBookmarkMapper {

    public ArtworkBookmark toArtworkBookmark(User user, Artwork artwork) {
        return ArtworkBookmark.builder()
                .artwork(artwork)
                .user(user)
                .build();
    }

    public BookmarkToggleResponse toBookmarkToggleData(boolean isBookmarked){
        return BookmarkToggleResponse.builder()
                .isBookmarked(isBookmarked)
                .build();
    }
}
