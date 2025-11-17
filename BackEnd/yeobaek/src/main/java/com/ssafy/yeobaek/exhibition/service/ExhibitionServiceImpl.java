package com.ssafy.yeobaek.exhibition.service;

import com.ssafy.yeobaek.artwork.entity.Artwork;
import com.ssafy.yeobaek.artwork.repository.ArtworkRepository;
import com.ssafy.yeobaek.cafe.entity.Cafe;
import com.ssafy.yeobaek.cafe.entity.DisplaySlot;
import com.ssafy.yeobaek.cafe.repository.CafeRepository;
import com.ssafy.yeobaek.cafe.repository.DisplaySlotRepository;
import com.ssafy.yeobaek.chat.dto.response.ChatRoomData;
import com.ssafy.yeobaek.chat.service.ChatService;
import com.ssafy.yeobaek.common.code.ResponseCode;
import com.ssafy.yeobaek.common.enums.ExhibitionState;
import com.ssafy.yeobaek.common.enums.MessageType;
import com.ssafy.yeobaek.common.enums.UserRole;
import com.ssafy.yeobaek.common.exception.CustomException;
import com.ssafy.yeobaek.exhibition.dto.request.CreateExhibitionRequest;
import com.ssafy.yeobaek.exhibition.dto.response.ExhibitionSummaryResponse;
import com.ssafy.yeobaek.exhibition.entity.Exhibition;
import com.ssafy.yeobaek.exhibition.mapper.ExhibitionMapper;
import com.ssafy.yeobaek.exhibition.repository.ExhibitionRepository;
import com.ssafy.yeobaek.user.entity.User;
import com.ssafy.yeobaek.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExhibitionServiceImpl implements ExhibitionService {

    private final ExhibitionRepository exhibitionRepository;
    private final ExhibitionMapper exhibitionMapper;
    private final ChatService chatService; // ChatService 주입

    // TODO: 순환 참조 문제 해결 후 Service 의존성 변경
    // 다른 도메인의 Repository 의존성
    private final UserRepository userRepository;
    private final CafeRepository cafeRepository;
    private final ArtworkRepository artworkRepository;
    private final DisplaySlotRepository displaySlotRepository;

    /**
     * 전시 제안 생성
     * - 카페 사장 또는 작가가 전시를 제안
     * - 제안자는 자동으로 동의 상태가 되며, 상대방이 동의하면 전시가 확정
     * - 전시 기간 검증 및 슬롯/작품 중복 체크 수행
     *
     * @param userId 현재 사용자 ID (제안자)
     * @param request 전시 제안 요청 정보
     */
    @Override
    @Transactional
    public void createExhibitionProposal(Long userId, CreateExhibitionRequest request) {

        // 1. 현재 사용자 조회
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ResponseCode.USER_NOT_FOUND));

        // 2. Cafe, Artwork, DisplaySlot 조회
        Cafe cafe = cafeRepository.findById(request.getCafeId())
                .orElseThrow(() -> new CustomException(ResponseCode.CAFE_NOT_FOUND));

        Artwork artwork = artworkRepository.findById(request.getArtworkId())
                .orElseThrow(() -> new CustomException(ResponseCode.ARTWORK_NOT_FOUND));

        DisplaySlot displaySlot = displaySlotRepository.findById(request.getSlotId())
                .orElseThrow(() -> new CustomException(ResponseCode.SLOT_NOT_FOUND));

        // 3. 슬롯이 해당 카페에 속하는지 확인
        if (!displaySlot.getWall().getCafe().getId().equals(cafe.getId())) {
            throw new CustomException(ResponseCode.SLOT_NOT_FOUND_IN_CAFE);
        }

        // 4. 전시 기간 검증
        LocalDate startDate = request.getStartDate();
        LocalDate endDate = request.getEndDate();

        if (endDate.isBefore(startDate)) {
            throw new CustomException(ResponseCode.EXHIBITION_INVALID_EXHIBITION_DATE);
        }

        if (startDate.isBefore(LocalDate.now())) {
            throw new CustomException(ResponseCode.EXHIBITION_DATE_PAST);
        }

        // 5. 중복 체크 (같은 기간에 슬롯이나 작품이 이미 예약되어 있는지)
        boolean slotConflict = exhibitionRepository.existsConflictingSlot(request.getSlotId(), startDate, endDate);
        if (slotConflict) {
            throw new CustomException(ResponseCode.SLOT_ALREADY_RESERVED);
        }

        boolean artworkConflict = exhibitionRepository.existsConflictingArtwork(request.getArtworkId(), startDate, endDate);
        if (artworkConflict) {
            throw new CustomException(ResponseCode.ARTWORK_ALREADY_RESERVED);
        }

        // 6. 역할별 권한 검증 및 동의 상태 설정
        boolean cafeAgreed = false;
        boolean artistAgreed = false;

        if (currentUser.getUserType() == UserRole.CAFE_OWNER) {
            // 카페 사장이 제안
            if (!cafe.getId().equals(userId)) {
                throw new CustomException(ResponseCode.CAFE_FORBIDDEN_UPDATE_DELETE);
            }
            cafeAgreed = true;  // 제안자가 먼저 동의

        } else if (currentUser.getUserType() == UserRole.ARTIST) {
            // 작가가 제안
            if (!artwork.getArtist().getUser().getId().equals(userId)) {
                throw new CustomException(ResponseCode.ARTWORK_FORBIDDEN);
            }
            artistAgreed = true;  // 제안자가 먼저 동의

        } else {
            throw new CustomException(ResponseCode.FORBIDDEN);
        }

        // 7. Exhibition 엔티티 생성 (Mapper 사용)
        Exhibition exhibition = exhibitionMapper.toExhibition(
                request, cafe, artwork, displaySlot, cafeAgreed, artistAgreed
        );

        // 8. 저장
        exhibitionRepository.save(exhibition);

        // 9. 채팅방 ID 파생 및 시스템 메시지 전송
        Long cafeOwnerId = cafe.getId();
        Long artistId = artwork.getArtist().getUser().getId();
        ChatRoomData chatRoomData = chatService.createChatRoom(cafeOwnerId, artistId);
        Long chatRoomId = chatRoomData.getChatRoom().getChatRoomId();

        chatService.sendSystemMessage(chatRoomId, null, MessageType.PROPOSAL, exhibition, currentUser);
    }

    /**
     * 전시 제안 동의
     * - PENDING 상태의 전시만 동의 가능
     * - 카페 사장: 본인 카페의 전시인지 확인 후 동의
     * - 작가: 본인 작품의 전시인지 확인 후 동의
     * - 양쪽 모두 동의 시 자동으로 APPROVED 상태로 변경
     *
     * @param userId 현재 사용자 ID
     * @param exhibitionId 전시 ID
     */
    @Override
    @Transactional
    public void agreeExhibition(Long userId, Long exhibitionId) {
        // 1. 전시 조회 및 PENDING 상태 검증
        Exhibition exhibition = validatePendingExhibition(exhibitionId);

        // 2. 현재 사용자 조회
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ResponseCode.USER_NOT_FOUND));

        // 3. 권한 확인 및 동의 처리
        if (currentUser.getUserType() == UserRole.CAFE_OWNER) {
            // 카페 사장: 해당 전시의 카페 소유자인지 확인
            if (!exhibition.getCafe().getId().equals(userId)) {
                throw new CustomException(ResponseCode.EXHIBITION_FORBIDDEN);
            }

            // 이미 동의했는지 확인
            if (exhibition.getCafeAgreed()) {
                throw new CustomException(ResponseCode.EXHIBITION_ALREADY_AGREED);
            }

            // 카페 사장 동의 처리
            exhibition.agreeByCafe();

        } else if (currentUser.getUserType() == UserRole.ARTIST) {
            // 작가: 해당 전시의 작품 작가인지 확인
            if (!exhibition.getArtwork().getArtist().getUser().getId().equals(userId)) {
                throw new CustomException(ResponseCode.EXHIBITION_FORBIDDEN);
            }

            // 이미 동의했는지 확인
            if (exhibition.getArtistAgreed()) {
                throw new CustomException(ResponseCode.EXHIBITION_ALREADY_AGREED);
            }

            // 작가 동의 처리
            exhibition.agreeByArtist();

        } else {
            // 카페 사장도 작가도 아닌 경우
            throw new CustomException(ResponseCode.EXHIBITION_FORBIDDEN);
        }

        // 4. 양측 모두 동의하여 전시가 확정된 경우 시스템 메시지 전송
        if (exhibition.getState() == ExhibitionState.APPROVED) {
            Long cafeOwnerId = exhibition.getCafe().getId();
            Long artistId = exhibition.getArtwork().getArtist().getUser().getId();
            ChatRoomData chatRoomData = chatService.createChatRoom(cafeOwnerId, artistId);
            Long chatRoomId = chatRoomData.getChatRoom().getChatRoomId();

            chatService.sendSystemMessage(chatRoomId, null, MessageType.AGREED, exhibition, currentUser);
        }
    }

    /**
     * 전시 제안 거절
     * - PENDING 상태의 전시만 거절 가능
     * - 카페 사장 또는 작가만 거절 가능
     * - 거절 시 전시가 REJECTED 상태로 변경
     *
     * @param userId 현재 사용자 ID
     * @param exhibitionId 전시 ID
     */
    @Override
    @Transactional
    public void rejectExhibition(Long userId, Long exhibitionId) {
        // 1. 전시 조회 및 PENDING 상태 검증
        Exhibition exhibition = validatePendingExhibition(exhibitionId);

        // 2. 현재 사용자 조회
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ResponseCode.USER_NOT_FOUND));

        // 3. 권한 확인
        boolean isCafeOwner = currentUser.getUserType() == UserRole.CAFE_OWNER
                && exhibition.getCafe().getId().equals(userId);
        boolean isArtist = currentUser.getUserType() == UserRole.ARTIST
                && exhibition.getArtwork().getArtist().getUser().getId().equals(userId);

        if (!isCafeOwner && !isArtist) {
            throw new CustomException(ResponseCode.EXHIBITION_FORBIDDEN);
        }

        // 4. 거절 처리
        exhibition.reject();

        // 5. 채팅방 ID 파생 및 시스템 메시지 전송
        Long cafeOwnerId = exhibition.getCafe().getId();
        Long artistId = exhibition.getArtwork().getArtist().getUser().getId();
        ChatRoomData chatRoomData = chatService.createChatRoom(cafeOwnerId, artistId);
        Long chatRoomId = chatRoomData.getChatRoom().getChatRoomId();

        chatService.sendSystemMessage(chatRoomId, null, MessageType.REJECTED, exhibition, currentUser);
    }

    /**
     * 전시 조회 및 PENDING 상태 검증
     * @param exhibitionId 전시 ID
     * @return 검증된 Exhibition 엔티티
     */
    private Exhibition validatePendingExhibition(Long exhibitionId) {
        Exhibition exhibition = exhibitionRepository.findById(exhibitionId)
                .orElseThrow(() -> new CustomException(ResponseCode.EXHIBITION_NOT_FOUND));

        if (exhibition.getState() != ExhibitionState.PENDING) {
            throw new CustomException(ResponseCode.EXHIBITION_NOT_PENDING);
        }

        return exhibition;
    }

    /**
     * 특정 작품의 현재 진행 중인 전시 조회
     * - 오늘 날짜 기준으로 진행 중인 전시 반환
     * - 전시가 없으면 Optional.empty() 반환
     *
     * @param artworkId 작품 ID
     * @return 현재 진행 중인 전시
     */
    @Override
    public Optional<Exhibition> getCurrentExhibitionByArtwork(Long artworkId) {
        LocalDate today = LocalDate.now();
        return exhibitionRepository.findCurrentExhibitionByArtworkId(artworkId, today);
    }

    /**
     * 슬롯의 현재 또는 예정된 전시 조회
     * @param displaySlot 조회할 슬롯
     * @return 현재 진행 중이거나 예정된 전시 중 가장 빠른 전시
     */
    @Override
    public Optional<Exhibition> findCurrentOrUpcomingExhibitionBySlot(DisplaySlot displaySlot) {
        return exhibitionRepository.findFirstByDisplaySlotAndStateInOrderByStartDateAsc(
                displaySlot, List.of(ExhibitionState.ONGOING, ExhibitionState.APPROVED)
        );
    }

    /**
     * 슬롯의 현재 진행 중인 전시 조회
     * @param displaySlot 조회할 슬롯
     * @return 현재 진행 중인 전시
     */
    @Override
    public Optional<Exhibition> findOngoingExhibitionBySlot(DisplaySlot displaySlot) {
        return exhibitionRepository.findFirstByDisplaySlotAndStateInOrderByStartDateAsc(displaySlot, List.of(ExhibitionState.ONGOING));
    }

    /**
     * 특정 작가의 현재 전시 중인 작품 수 조회
     * - 오늘 날짜 기준으로 전시 중인 작품 수 반환
     *
     * @param artistId 작가 ID
     * @return 현재 전시 중인 작품 수
     */
    @Override
    public Integer getCurrentExhibitionCountByArtist(Long artistId) {
        LocalDate today = LocalDate.now();
        return exhibitionRepository.countCurrentExhibitionsByArtistId(artistId, today);
    }

    /**
     * 전시 ID로 전시 상세 정보 조회
     * - 전시의 기본 정보를 반환
     * - 카페, 작품, 슬롯의 간략한 정보 포함
     *
     * @param exhibitionId 전시 ID
     * @return 전시 상세 정보
     */
    @Override
    public ExhibitionSummaryResponse getExhibitionSummary(Long exhibitionId) {
        // 1. 전시 조회
        Exhibition exhibition = exhibitionRepository.findById(exhibitionId)
                .orElseThrow(() -> new CustomException(ResponseCode.EXHIBITION_NOT_FOUND));

        // 2. DTO 변환 및 반환
        return exhibitionMapper.toExhibitionSummaryResponse(exhibition);
    }

    /**
     * 슬롯별 전시 일정 목록 조회
     * - 특정 날짜 이후의 전시 일정 목록을 반환
     * - APPROVED 또는 ONGOING 상태인 전시만 조회
     * - 시작일 기준 오름차순 정렬
     *
     * @param slotId 슬롯 ID
     * @param from 조회 기준 날짜
     * @return 전시 일정 목록
     */
    @Override
    public List<ExhibitionSummaryResponse> getExhibitionsBySlot(Long slotId, LocalDate from) {
        // 1. 슬롯 존재 여부 확인
        DisplaySlot displaySlot = displaySlotRepository.findById(slotId)
                .orElseThrow(() -> new CustomException(ResponseCode.SLOT_NOT_FOUND));

        // 2. 전시 목록 조회
        List<Exhibition> exhibitions = exhibitionRepository.findBySlotIdAndFromDate(slotId, from);

        // 3. DTO 변환 및 반환
        return exhibitions.stream()
                .map(exhibitionMapper::toSlotExhibitionSummaryResponse)
                .toList();
    }
}
