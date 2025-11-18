package com.ssafy.yeobaek.user.service;

import com.ssafy.yeobaek.artwork.repository.ArtworkBookmarkRepository;
import com.ssafy.yeobaek.artwork.repository.ArtworkRepository;
import com.ssafy.yeobaek.auth.dto.request.SignupRequest;
import com.ssafy.yeobaek.cafe.entity.Cafe;
import com.ssafy.yeobaek.cafe.repository.CafeBookmarkRepository;
import com.ssafy.yeobaek.cafe.repository.CafeRepository;
import com.ssafy.yeobaek.common.code.ResponseCode;
import com.ssafy.yeobaek.common.enums.Gender;
import com.ssafy.yeobaek.common.enums.UserRole;
import com.ssafy.yeobaek.common.exception.CustomException;
import com.ssafy.yeobaek.exhibition.repository.ExhibitionRepository;
import com.ssafy.yeobaek.review.entity.Review;
import com.ssafy.yeobaek.review.repository.ReviewRepository;
import com.ssafy.yeobaek.storage.service.S3Service;
import com.ssafy.yeobaek.user.dto.request.UpdateProfileRequest;
import com.ssafy.yeobaek.user.dto.response.ArtistData;
import com.ssafy.yeobaek.user.dto.response.CafeData;
import com.ssafy.yeobaek.user.dto.response.UserData;
import com.ssafy.yeobaek.user.dto.response.UserProfileData;
import com.ssafy.yeobaek.user.entity.Artist;
import com.ssafy.yeobaek.user.entity.User;
import com.ssafy.yeobaek.user.mapper.UserMapper;
import com.ssafy.yeobaek.user.repository.ArtistRepository;
import com.ssafy.yeobaek.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ArtistRepository artistRepository;
    private final ArtworkRepository artworkRepository;
    private final ExhibitionRepository exhibitionRepository;
    private final ReviewRepository reviewRepository;
    private final CafeRepository cafeRepository;
    private final CafeBookmarkRepository cafeBookmarkRepository;
    private final ArtworkBookmarkRepository artworkBookmarkRepository;
    private final UserMapper userMapper;
    private final S3Service s3Service;

    /**
     * 이메일 중복 체크
     * @param email 확인할 이메일
     * @return 이메일이 이미 존재하면 true, 사용 가능하면 false
     */
    @Override
    public boolean isEmailExists(String email) {
        log.debug("이메일 존재 여부 확인 - 이메일: {}", email);
        return userRepository.existsByEmail(email);
    }

    /**
     * 이메일로 사용자 조회
     * @param email 조회할 이메일
     * @return 사용자 엔티티 (Optional)
     */
    @Override
    public Optional<User> findByEmail(String email) {
        log.debug("이메일로 사용자 조회 - 이메일: {}", email);
        return userRepository.findByEmail(email);
    }

    /**
     * ID로 사용자 조회
     * @param id 사용자 ID
     * @return 사용자 엔티티 (Optional)
     */
    @Override
    public Optional<User> findById(Long id) {
        log.debug("ID로 사용자 조회 - ID: {}", id);
        return userRepository.findById(id);
    }

    /**
     * 내 프로필 조회
     * @param userId 사용자 ID
     * @return 사용자 프로필 데이터
     */
    @Override
    public UserProfileData getMyProfile(Long userId) {
        log.info("내 프로필 조회 시작 - 사용자 ID: {}", userId);
        // 사용자 조회
        User user = findById(userId)
                .orElseThrow(() -> {
                    log.warn("내 프로필 조회 실패 - 존재하지 않는 사용자 ID: {}", userId);
                    return new CustomException(ResponseCode.USER_NOT_FOUND);
                });

        // S3 presigned URL 변환
        String profileImageUrl = s3Service.generatePresignedUrlForThumbnail(user.getProfileImageUrl());

        // 북마크 수 조회 (카페 북마크 + 작품 북마크)
        int bookmarkCount = getTotalBookmarkCount(userId);

        // 사용자 기본 정보 변환
        UserData userData = userMapper.toUserData(user, profileImageUrl, bookmarkCount);

        // UserType에 따라 추가 정보 생성
        ArtistData artistData = user.getUserType() == UserRole.ARTIST
                ? getArtistData(userId)
                : null;

        CafeData cafeData = user.getUserType() == UserRole.CAFE_OWNER
                ? getCafeData(userId)
                : null;

        log.info("내 프로필 조회 완료 - 사용자 ID: {}", userId);
        return UserProfileData.builder()
                .user(userData)
                .artist(artistData)
                .cafe(cafeData)
                .build();
    }

    /**
     * 특정 사용자 공개 프로필 조회
     * @param userId 조회할 사용자 ID
     * @return 공개 프로필 데이터
     */
    @Override
    public UserProfileData getUserProfile(Long userId) {
        log.info("사용자 프로필 조회 시작 - 조회 대상 ID: {}", userId);
        // 사용자 조회
        User user = findById(userId)
                .orElseThrow(() -> {
                    log.warn("사용자 프로필 조회 실패 - 존재하지 않는 사용자 ID: {}", userId);
                    return new CustomException(ResponseCode.USER_NOT_FOUND);
                });

        // S3 presigned URL 변환
        String profileImageUrl = s3Service.generatePresignedUrlForThumbnail(user.getProfileImageUrl());

        // 공개 프로필용 사용자 기본 정보 변환 (userId, nickname, userType, profileImageUrl만 공개)
        UserData userData = userMapper.toPublicUserData(user, profileImageUrl);

        // UserType에 따라 추가 정보 생성 (artist와 cafe 정보는 전부 공개)
        ArtistData artistData = user.getUserType() == UserRole.ARTIST
                ? getArtistData(userId)
                : null;

        CafeData cafeData = user.getUserType() == UserRole.CAFE_OWNER
                ? getCafeData(userId)
                : null;

        log.info("사용자 프로필 조회 완료 - 조회 대상 ID: {}", userId);
        return UserProfileData.builder()
                .user(userData)
                .artist(artistData)
                .cafe(cafeData)
                .build();
    }

    /**
     * 프로필 정보 수정
     * @param userId 사용자 ID
     * @param request 수정할 프로필 정보
     */
    @Override
    @Transactional
    public void updateProfile(Long userId, UpdateProfileRequest request) {
        log.info("프로필 수정 시작 - 사용자 ID: {}", userId);
        // 사용자 조회
        User user = findById(userId)
                .orElseThrow(() -> {
                    log.warn("프로필 수정 실패 - 존재하지 않는 사용자 ID: {}", userId);
                    return new CustomException(ResponseCode.USER_NOT_FOUND);
                });

        // 닉네임 수정
        if (request.getNickname() != null) {
            log.info("닉네임 변경: '{}' -> '{}'", user.getNickname(), request.getNickname());
            user.setNickname(request.getNickname());
        }

        // 프로필 이미지 URL 수정
        if (request.getProfileImageUrl() != null) {
            log.info("프로필 이미지 변경");
            user.setProfileImageUrl(request.getProfileImageUrl());
        }

        // 작가 소개(introduction) 수정
        if (user.getUserType() == UserRole.ARTIST && request.getIntroduction() != null) {
            Artist artist = artistRepository.findById(userId)
                    .orElseThrow(() -> {
                        log.error("프로필 수정 실패 - ARTIST 유저이지만 Artist 엔티티가 존재하지 않음. 사용자 ID: {}", userId);
                        return new CustomException(ResponseCode.USER_NOT_FOUND);
                    });
            log.info("작가 소개 변경");
            artist.setIntroduction(request.getIntroduction());
            artistRepository.save(artist);
        }

        // 변경 사항 저장
        userRepository.save(user);
        log.info("프로필 수정 완료 - 사용자 ID: {}", userId);
    }

    /**
     * Artist 프록시 객체 조회
     * @param artistId 작가 ID
     * @return Artist 프록시 객체
     */
    @Override
    public Artist getArtistReference(Long artistId) {
        log.debug("Artist 프록시 객체 조회 - 작가 ID: {}", artistId);
        return artistRepository.getReferenceById(artistId);
    }

    /**
     * 회원가입 - 사용자 생성 및 저장
     * @param request         회원가입 요청 정보
     * @param encodedPassword 암호화된 비밀번호
     */
    @Override
    @Transactional
    public void createUser(SignupRequest request, String encodedPassword) {
        log.info("신규 사용자 생성 시작 - 이메일: {}, 유형: {}", request.getEmail(), request.getUserType());
        // User 엔티티 생성
        User user = User.builder()
                .userType(UserRole.valueOf(request.getUserType()))
                .email(request.getEmail())
                .password(encodedPassword)
                .nickname(request.getNickname())
                .birthDate(LocalDate.parse(request.getBirthDate()))
                .gender(Gender.valueOf(request.getGender()))
                .build();

        // User 저장
        User savedUser = userRepository.save(user);
        log.info("User 엔티티 저장 완료 - 사용자 ID: {}", savedUser.getId());

        // ARTIST 타입인 경우 Artist 엔티티 자동 생성
        if (user.getUserType() == UserRole.ARTIST) {
            Artist artist = Artist.builder()
                    .user(user)
                    .introduction(null)  // 초기값 null
                    .build();
            artistRepository.save(artist);
            log.info("Artist 엔티티 자동 생성 완료 - 작가 ID: {}", artist.getUser().getId());
        }
    }

    /**
     * 작가명으로 검색
     * @param keyword 검색 키워드
     * @return 작가명에 키워드가 포함된 작가 목록
     */
    @Override
    public List<Artist> searchArtistsByKeyword(String keyword) {
        log.info("작가명으로 검색 시작 - 키워드: '{}'", keyword);
        // ARTIST 타입인 사용자 중 닉네임에 키워드가 포함된 사용자 검색
        List<User> users = userRepository.findByNicknameContainingAndUserType(keyword, UserRole.ARTIST);

        // User ID로 Artist 엔티티 조회
        List<Artist> artists = users.stream()
                .map(user -> artistRepository.findById(user.getId()).orElse(null))
                .filter(artist -> artist != null)
                .collect(java.util.stream.Collectors.toList());
        log.info("작가명 검색 완료 - '{}'에 대해 {}명 검색됨", keyword, artists.size());
        return artists;
    }

    /**
     * 작가 존재 여부 확인
     * @param artistId 작가 ID
     * @return 작가가 존재하면 true, 아니면 false
     */
    @Override
    public boolean existsArtistById(Long artistId) {
        log.debug("작가 존재 여부 확인 - 작가 ID: {}", artistId);
        return artistRepository.existsById(artistId);
    }

    /**
     * Artist 정보 조회 (통계 포함)
     * @param artistId 작가 ID (= 사용자 ID)
     * @return ArtistData DTO
     */
    private ArtistData getArtistData(Long artistId) {
        log.debug("작가 추가 정보 조회 시작 - 작가 ID: {}", artistId);
        // Artist 엔티티 조회
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> {
                    log.error("작가 추가 정보 조회 실패 - Artist 엔티티를 찾을 수 없음. 작가 ID: {}", artistId);
                    return new CustomException(ResponseCode.USER_NOT_FOUND);
                });

        // 작품 수 조회
        int artworkCount = artworkRepository.countByArtist(artist);

        // 전시 중인 작품 수 조회
        int exhibitingCount = artworkRepository.countByArtistAndIsExhibiting(artist, true);

        // 총 전시 완료 횟수 조회
        Integer totalExhibitedCount = exhibitionRepository.countCompletedExhibitionsByArtistId(artistId);

        // 리뷰 통계 계산
        List<Review> reviews = reviewRepository.findAllByRevieweeId(artistId);
        int totalReviews = reviews.size();
        double averageRating = reviews.isEmpty() ? 0.0 :
                reviews.stream()
                        .mapToInt(Review::getRating)
                        .average()
                        .orElse(0.0);

        // ArtistData DTO 생성
        return userMapper.toArtistData(
                artist,
                artworkCount,
                exhibitingCount,
                totalExhibitedCount != null ? totalExhibitedCount : 0,
                totalReviews,
                averageRating
        );
    }

    /**
     * Cafe 정보 조회 (통계 포함)
     * @param cafeOwnerId 카페 사장 ID (= 사용자 ID)
     * @return CafeData DTO
     */
    private CafeData getCafeData(Long cafeOwnerId) {
        log.debug("카페 추가 정보 조회 시작 - 카페 사장 ID: {}", cafeOwnerId);
        // Cafe 엔티티 조회
        Cafe cafe = cafeRepository.findByCafeOwnerId(cafeOwnerId)
                .orElse(null);

        if(cafe == null) {
            log.warn("카페 추가 정보 조회 - 해당 사용자의 카페가 존재하지 않음. 카페 사장 ID: {}", cafeOwnerId);
            return null;
        }

        // 리뷰 통계 계산
        List<Review> reviews = reviewRepository.findAllByRevieweeId(cafeOwnerId);
        int totalReviews = reviews.size();
        double averageRating = reviews.isEmpty() ? 0.0 :
                reviews.stream()
                        .mapToInt(Review::getRating)
                        .average()
                        .orElse(0.0);

        // CafeData DTO 생성
        return userMapper.toCafeData(
                cafe,
                totalReviews,
                averageRating
        );
    }

    /**
     * 사용자의 전체 북마크 수 조회
     * @param userId 사용자 ID
     * @return 전체 북마크 수 (카페 북마크 + 작품 북마크)
     */
    private int getTotalBookmarkCount(Long userId) {
        log.debug("전체 북마크 수 조회 - 사용자 ID: {}", userId);
        long cafeBookmarks = cafeBookmarkRepository.countByUser_Id(userId);
        long artworkBookmarks = artworkBookmarkRepository.countByUser_Id(userId);
        return (int) (cafeBookmarks + artworkBookmarks);
    }
}
