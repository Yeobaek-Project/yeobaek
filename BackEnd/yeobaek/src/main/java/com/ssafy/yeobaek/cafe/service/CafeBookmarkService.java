package com.ssafy.yeobaek.cafe.service;

import com.ssafy.yeobaek.cafe.dto.response.BookmarkToggleResponse;

public interface CafeBookmarkService {

    /**
     * 사용자가 특정 카페를 북마크했는지 확인
     * @param userId 사용자 ID
     * @param cafeId 카페 ID
     * @return 북마크 여부
     */
    boolean isBookmarked(Long userId, Long cafeId);

    /**
     * 카페 북마크 토글
     * @param userId 사용자 ID
     * @param cafeId 카페 ID
     * @return BookmarkToggleData (현재 북마크 상태)
     */
    BookmarkToggleResponse toggleBookmark(Long userId, Long cafeId);
}
