package com.ssafy.yeobaek.cafe.service;

import com.ssafy.yeobaek.analytics.enums.TargetType;
import com.ssafy.yeobaek.analytics.service.AnalyticsService;
import com.ssafy.yeobaek.cafe.dto.response.BookmarkToggleResponse;
import com.ssafy.yeobaek.cafe.entity.Cafe;
import com.ssafy.yeobaek.cafe.entity.CafeBookmark;
import com.ssafy.yeobaek.cafe.mapper.CafeBookmarkMapper;
import com.ssafy.yeobaek.cafe.repository.CafeBookmarkRepository;
import com.ssafy.yeobaek.cafe.repository.CafeRepository;
import com.ssafy.yeobaek.common.code.ResponseCode;
import com.ssafy.yeobaek.common.exception.CustomException;
import com.ssafy.yeobaek.user.entity.User;
import com.ssafy.yeobaek.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CafeBookmarkServiceImpl implements CafeBookmarkService {

    private final CafeBookmarkRepository cafeBookmarkRepository;
    private final CafeRepository cafeRepository;
    private final UserService userService;
    private final CafeBookmarkMapper cafeBookmarkMapper;
    private final AnalyticsService analyticsService;

    /**
     * 사용자가 특정 카페를 북마크했는지 확인
     * @param userId 사용자 ID
     * @param cafeId 카페 ID
     * @return 북마크 여부
     */
    @Override
    public boolean isBookmarked(Long userId, Long cafeId) {
        return cafeBookmarkRepository.existsByUser_IdAndCafe_Id(userId, cafeId);
    }

    /**
     * 카페 북마크 토글
     * @param userId 사용자 ID
     * @param cafeId 카페 ID
     * @return BookmarkToggleData (현재 북마크 상태)
     */
    @Override
    @Transactional
    public BookmarkToggleResponse toggleBookmark(Long userId, Long cafeId) {

        // 북마크 여부 확인
        boolean isBookmarked = isBookmarked(userId, cafeId);

        // 북마크 토글 (북마크면 삭제, 아니면 생성)
        if(isBookmarked)
            deleteBookmark(userId, cafeId);
        else
            createBookmark(userId, cafeId);

        return cafeBookmarkMapper.toBookmarkToggleData(!isBookmarked);
    }

    /**
     * 카페 북마크 생성
     * @param userId 사용자 ID
     * @param cafeId 카페 ID
     */
    private void createBookmark(Long userId, Long cafeId){

        // 사용자 정보 조회
        User user = userService.findById(userId)
                .orElseThrow(() -> new CustomException(ResponseCode.PROFILE_USER_NOT_FOUND));

        // 카페 조회
        Cafe cafe = cafeRepository.findById(cafeId)
                .orElseThrow(() -> new CustomException(ResponseCode.CAFE_NOT_FOUND));

        // 북마크 수 증가
        cafe.increaseBookmarkCount();

        // CafeBookmark 엔티티 변환 및 저장
        CafeBookmark cafeBookmark = cafeBookmarkMapper.toCafeBookmark(user, cafe);
        cafeBookmarkRepository.save(cafeBookmark);

        // 북마크 이벤트 기록
        analyticsService.recordBookmarkEvent(userId, cafeId, TargetType.CAFE);
    }

    /**
     * 카페 북마크 삭제
     * @param userId 사용자 ID
     * @param cafeId 카페 ID
     */
    private void deleteBookmark(Long userId, Long cafeId){
        // 카페 조회
        Cafe cafe = cafeRepository.findById(cafeId)
                .orElseThrow(() -> new CustomException(ResponseCode.CAFE_NOT_FOUND));

        // 북마크 수 감소
        cafe.decreaseBookmarkCount();

        // 북마크 삭제
        cafeBookmarkRepository.deleteCafeBookmarkByUser_IdAndCafe_Id(userId, cafeId);
    }
}
