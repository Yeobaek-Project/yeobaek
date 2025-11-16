package com.ssafy.yeobaek.artwork.service;

import com.ssafy.yeobaek.analytics.enums.TargetType;
import com.ssafy.yeobaek.analytics.service.AnalyticsService;
import com.ssafy.yeobaek.artwork.dto.response.BookmarkToggleResponse;
import com.ssafy.yeobaek.artwork.entity.Artwork;
import com.ssafy.yeobaek.artwork.entity.ArtworkBookmark;
import com.ssafy.yeobaek.artwork.mapper.ArtworkBookmarkMapper;
import com.ssafy.yeobaek.artwork.repository.ArtworkBookmarkRepository;
import com.ssafy.yeobaek.artwork.repository.ArtworkRepository;
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
public class ArtworkBookmarkServiceImpl implements ArtworkBookmarkService {

    private final ArtworkBookmarkRepository artworkBookmarkRepository;
    private final ArtworkRepository artworkRepository;
    private final UserService userService;
    private final ArtworkBookmarkMapper artworkBookmarkMapper;
    private final AnalyticsService analyticsService;

    /**
     * 사용자가 특정 작품을 북마크했는지 확인
     * @param userId 사용자 ID
     * @param artworkId 작품 ID
     * @return 북마크 여부
     */
    @Override
    public boolean isBookmarked(Long userId, Long artworkId) {
        return artworkBookmarkRepository.existsByUser_IdAndArtwork_Id(userId, artworkId);
    }

    /**
     * 작품 북마크 토글
     * @param userId    사용자 ID
     * @param artworkId 작품 ID
     * @return BookmarkToggleData (현재 북마크 상태)
     */
    @Override
    @Transactional
    public BookmarkToggleResponse toggleBookmark(Long userId, Long artworkId) {

        // 북마크 여부 확인
        boolean isBookmarked = isBookmarked(userId, artworkId);

        // 북마크 토글 (북마크면 삭제, 아니면 생성)
        if(isBookmarked)
            deleteBookmark(userId, artworkId);
        else
            createBookmark(userId, artworkId);

        return artworkBookmarkMapper.toBookmarkToggleData(!isBookmarked);
    }

    /**
     * 작품 북마크 생성
     * @param userId 사용자 ID
     * @param artworkId 작품 ID
     */
    private void createBookmark(Long userId, Long artworkId){

        // 사용자 정보 조회
        User user = userService.findById(userId)
                .orElseThrow(() -> new CustomException(ResponseCode.PROFILE_USER_NOT_FOUND));

        // 작품 조회
        Artwork artwork = artworkRepository.findById(artworkId)
                .orElseThrow(() -> new CustomException(ResponseCode.ARTWORK_NOT_FOUND));

        // ArtworkBookmark 엔티티 변환 및 저장
        ArtworkBookmark artworkBookmark = artworkBookmarkMapper.toArtworkBookmark(user, artwork);
        artworkBookmarkRepository.save(artworkBookmark);

        // 북마크 카운트 증가
        artwork.incrementBookmarkCount();

        // 북마크 이벤트 기록
        analyticsService.recordBookmarkEvent(userId, artworkId, TargetType.ARTWORK);
    }

    /**
     * 작품 북마크 삭제
     * @param userId 사용자 ID
     * @param artworkId 북마크 ID
     */
    private void deleteBookmark(Long userId, Long artworkId){
        // 작품 조회
        Artwork artwork = artworkRepository.findById(artworkId)
                .orElseThrow(() -> new CustomException(ResponseCode.ARTWORK_NOT_FOUND));

        // 북마크 삭제
        artworkBookmarkRepository.deleteArtworkBookmarkByUser_IdAndArtwork_Id(userId, artworkId);

        // 북마크 카운트 감소
        artwork.decrementBookmarkCount();
    }
}
