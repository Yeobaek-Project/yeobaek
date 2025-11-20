package com.ssafy.yeobaek.cafe.mapper;

import com.ssafy.yeobaek.cafe.dto.response.BookmarkToggleResponse;
import com.ssafy.yeobaek.cafe.entity.Cafe;
import com.ssafy.yeobaek.cafe.entity.CafeBookmark;
import com.ssafy.yeobaek.user.entity.User;
import org.springframework.stereotype.Component;

/**
 * CafeBookmark 도메인 엔티티와 DTO 간의 변환을 담당하는 매퍼
 */
@Component
public class CafeBookmarkMapper {

    public CafeBookmark toCafeBookmark(User user, Cafe cafe) {
        return CafeBookmark.builder()
                .cafe(cafe)
                .user(user)
                .build();
    }

    public BookmarkToggleResponse toBookmarkToggleData(boolean isBookmarked){
        return BookmarkToggleResponse.builder()
                .isBookmarked(isBookmarked)
                .build();
    }
}
