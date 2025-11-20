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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
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
        log.info("카페 북마크 여부 확인 시작 - 사용자 ID: {}, 카페 ID: {}", userId, cafeId);
        boolean isBookmarked = cafeBookmarkRepository.existsByUser_IdAndCafe_Id(userId, cafeId);
        log.info("카페 북마크 여부 확인 완료 - 결과: {}", isBookmarked);
        return isBookmarked;
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
        log.info("카페 북마크 토글 시작 - 사용자 ID: {}, 카페 ID: {}", userId, cafeId);

        boolean isBookmarked = cafeBookmarkRepository.existsByUser_IdAndCafe_Id(userId, cafeId);
        log.info("현재 북마크 상태: {}", isBookmarked);

        if(isBookmarked) {
            log.info("북마크 삭제를 진행합니다.");
            deleteBookmark(userId, cafeId);
        } else {
            log.info("북마크 생성을 진행합니다.");
            createBookmark(userId, cafeId);
        }

        BookmarkToggleResponse response = cafeBookmarkMapper.toBookmarkToggleData(!isBookmarked);
        log.info("카페 북마크 토글 완료 - 최종 상태: {}", response.getIsBookmarked());
        return response;
    }

    /**
     * 카페 북마크 생성
     * @param userId 사용자 ID
     * @param cafeId 카페 ID
     */
    private void createBookmark(Long userId, Long cafeId){
        log.info("카페 북마크 생성 - 사용자 ID: {}, 카페 ID: {}", userId, cafeId);
        // 사용자 정보 조회
        User user = userService.findById(userId)
                .orElseThrow(() -> {
                    log.warn("카페 북마크 생성 실패 - 존재하지 않는 사용자 ID: {}", userId);
                    return new CustomException(ResponseCode.PROFILE_USER_NOT_FOUND);
                });

        // 카페 조회
        Cafe cafe = cafeRepository.findById(cafeId)
                .orElseThrow(() -> {
                    log.warn("카페 북마크 생성 실패 - 존재하지 않는 카페 ID: {}", cafeId);
                    return new CustomException(ResponseCode.CAFE_NOT_FOUND);
                });

        // 북마크 수 증가
        cafe.increaseBookmarkCount();

        // CafeBookmark 엔티티 변환 및 저장
        CafeBookmark cafeBookmark = cafeBookmarkMapper.toCafeBookmark(user, cafe);
        cafeBookmarkRepository.save(cafeBookmark);

        // 북마크 이벤트 기록
        analyticsService.recordBookmarkEvent(userId, cafeId, TargetType.CAFE);
        log.info("카페 북마크 생성 완료");
    }

    /**
     * 카페 북마크 삭제
     * @param userId 사용자 ID
     * @param cafeId 카페 ID
     */
    private void deleteBookmark(Long userId, Long cafeId){
        log.info("카페 북마크 삭제 - 사용자 ID: {}, 카페 ID: {}", userId, cafeId);
        // 카페 조회
        Cafe cafe = cafeRepository.findById(cafeId)
                .orElseThrow(() -> {
                    log.warn("카페 북마크 삭제 실패 - 존재하지 않는 카페 ID: {}", cafeId);
                    return new CustomException(ResponseCode.CAFE_NOT_FOUND);
                });

        // 북마크 수 감소
        cafe.decreaseBookmarkCount();

        // 북마크 삭제
        cafeBookmarkRepository.deleteCafeBookmarkByUser_IdAndCafe_Id(userId, cafeId);
        log.info("카페 북마크 삭제 완료");
    }
}
