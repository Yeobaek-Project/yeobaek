package com.ssafy.yeobaek.artwork.service;

import com.ssafy.yeobaek.artwork.dto.response.BookmarkToggleResponse;

public interface ArtworkBookmarkService {

    /**
     * 사용자가 특정 작품을 북마크했는지 확인
     * @param userId 사용자 ID
     * @param artworkId 작품 ID
     * @return 북마크 여부
     */
    boolean isBookmarked(Long userId, Long artworkId);

    /**
     * 작품 북마크 토글
     * @param userId 사용자 ID
     * @param artworkId 작품 ID
     * @return BookmarkToggleData (현재 북마크 상태)
     */
    BookmarkToggleResponse toggleBookmark(Long userId, Long artworkId);
}
