package com.ssafy.yeobaek.chat.service;

import com.ssafy.yeobaek.chat.dto.request.ChatMessageRequest;
import com.ssafy.yeobaek.chat.dto.response.*;
import com.ssafy.yeobaek.chat.entity.ChatRoom;
import com.ssafy.yeobaek.chat.entity.ChatRoomMember;
import com.ssafy.yeobaek.chat.entity.Message;
import com.ssafy.yeobaek.chat.mapper.ChatMapper;
import com.ssafy.yeobaek.chat.repository.ChatRoomMemberRepository;
import com.ssafy.yeobaek.chat.repository.ChatRoomRepository;
import com.ssafy.yeobaek.chat.repository.MessageRepository;
import com.ssafy.yeobaek.common.code.ResponseCode;
import com.ssafy.yeobaek.common.dto.Pagination;
import com.ssafy.yeobaek.common.enums.EventType;
import com.ssafy.yeobaek.common.enums.MessageType;
import com.ssafy.yeobaek.common.exception.CustomException;
import com.ssafy.yeobaek.exhibition.entity.Exhibition;
import com.ssafy.yeobaek.exhibition.repository.ExhibitionRepository;
import com.ssafy.yeobaek.storage.service.S3Service;
import com.ssafy.yeobaek.user.entity.User;
import com.ssafy.yeobaek.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageImpl;
import com.ssafy.yeobaek.common.enums.ChatSortBy;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatMapper chatMapper;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final MessageRepository messageRepository;
    private final ExhibitionRepository exhibitionRepository;
    private final UserService userService;
    private final S3Service s3Service;
    private final WebSocketMessageService webSocketMessageService;

    /**
     * 채팅방 생성
     * @param userId 사용자 ID
     * @param partnerId 상대방 ID
     * @return 생성된 채팅방 정보
     */
    @Override
    @Transactional
    public ChatRoomData createChatRoom(Long userId, Long partnerId) {

        // 1. 유효성 검증
        if (userId.equals(partnerId)) throw new CustomException(ResponseCode.MESSAGE_SELF_SEND_NOT_ALLOWED);

        // 2. 사용자 조회
        User user = userService.findById(userId)
                .orElseThrow(() -> new CustomException(ResponseCode.PROFILE_USER_NOT_FOUND));
        User partner = userService.findById(partnerId)
                .orElseThrow(() -> new CustomException(ResponseCode.PROFILE_USER_NOT_FOUND));

        // 3. 기존 1:1 채팅방 확인 (leftAt 상태와 상관없이 모든 멤버 조회)
        List<ChatRoomMember> senderRooms = chatRoomMemberRepository.findByUser(user);
        List<ChatRoomMember> recipientRooms = chatRoomMemberRepository.findByUser(partner);

        Set<ChatRoom> senderChatRooms = senderRooms.stream()
                .map(ChatRoomMember::getChatRoom)
                .collect(Collectors.toSet());

        Optional<ChatRoom> existingRoomOpt = recipientRooms.stream()
                .map(ChatRoomMember::getChatRoom)
                .filter(senderChatRooms::contains)
                .findFirst();

        ChatRoomData chatRoomData;

        // 4. 기존 채팅방이 있으면 해당 채팅방 정보 반환
        if (existingRoomOpt.isPresent()) {
            ChatRoom existingChatRoom = existingRoomOpt.get();

            // 현재 사용자의 ChatRoomMember를 찾아 leftAt을 null로 업데이트 (재참여)
            chatRoomMemberRepository.findByChatRoomAndUser_Id(existingChatRoom, userId)
                    .ifPresent(member -> {
                        if (member.getLeftAt() != null) {
                            member.setLeftAt(null);
                            chatRoomMemberRepository.save(member);
                        }
                    });

            String partnerImageUrl = s3Service.generatePresignedUrlForThumbnail(partner.getProfileImageUrl());
            chatRoomData = chatMapper.toChatRoomData(existingChatRoom, partner, partnerImageUrl);
        } else {
            // 5. 새로운 채팅방 생성
            ChatRoom newChatRoom = ChatRoom.builder().build();
            chatRoomRepository.save(newChatRoom);

            // 6. 채팅방 멤버 생성
            ChatRoomMember senderMember = ChatRoomMember.builder()
                    .chatRoom(newChatRoom)
                    .user(user)
                    .build();

            ChatRoomMember recipientMember = ChatRoomMember.builder()
                    .chatRoom(newChatRoom)
                    .user(partner)
                    .build();

            chatRoomMemberRepository.saveAll(List.of(senderMember, recipientMember));

            newChatRoom.setMembers(List.of(senderMember, recipientMember));

            // 7. 프로필 이미지 Url 발급
            String partnerImageUrl = s3Service.generatePresignedUrlForThumbnail(partner.getProfileImageUrl());

            chatRoomData = chatMapper.toChatRoomData(newChatRoom, partner, partnerImageUrl);

            // 8. 새로운 채팅방이 생성된 경우에만 상대방에게 채팅방 생성 이벤트 전송
            EventDto eventDto = EventDto.builder()
                    .eventType(EventType.CHAT_ROOM_CREATED)
                    .build();
            webSocketMessageService.sendEventToUser(partner.getEmail(), eventDto);
        }

        return chatRoomData;
    }

    /**
     * 특정 채팅방 메시지 조회
     * @param userId 사용자 ID
     * @param chatRoomId 채팅방 ID
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return 채팅방 정보
     */
    @Override
    public ChatRoomDetailData getChatRoomMessages(Long userId, Long chatRoomId, Integer page, Integer size) {

        // 1. 채팅방 조회
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new CustomException(ResponseCode.MESSAGE_ROOM_NOT_FOUND));

        // 2. 사용자 권한 확인 (채팅방 멤버인지 확인)
        chatRoomMemberRepository.findByChatRoomAndUser_Id(chatRoom, userId)
                .orElseThrow(() -> new CustomException(ResponseCode.MESSAGE_ROOM_FORBIDDEN));

        // 3. 상대방 정보 조회
        User partner = chatRoom.getMembers().stream()
                .map(ChatRoomMember::getUser)
                .filter(user -> !user.getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new CustomException(ResponseCode.PROFILE_USER_NOT_FOUND));

        String partnerImageUrl = s3Service.generatePresignedUrlForThumbnail(partner.getProfileImageUrl());

        // 4. 채팅방 요약 정보 생성
        ChatRoomSummary chatRoomSummary = chatMapper.toChatRoomSummary(chatRoom, partner, partnerImageUrl);

        // 5. 메시지 목록 조회 (페이지네이션 적용)
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
        Page<Message> messagePage = messageRepository.findByChatRoomWithExhibition(chatRoom, pageable);

        // 6. DTO 변환 (S3 URL 변환 포함)
        List<ChatMessage> chatMessages = messagePage.getContent().stream()
                .map(message -> {
                    ChatMessage chatMessage = chatMapper.toChatMessage(message);
                    if (message.getMessageType() == MessageType.IMAGE && message.getImageUrl() != null) {
                        String presignedUrl = s3Service.generatePresignedUrlForThumbnail(message.getImageUrl());
                        chatMessage.setImageUrl(presignedUrl);
                    }
                    return chatMessage;
                })
                .collect(Collectors.toList());

        // 7. 페이징 정보 생성
        Pagination pagination = new Pagination(
                messagePage.getNumber(),
                messagePage.getSize(),
                messagePage.getTotalElements(),
                messagePage.getTotalPages(),
                messagePage.isLast()
        );

        return new ChatRoomDetailData(chatRoomSummary, chatMessages, pagination);
    }

    /**
     * 채팅 메시지 전송
     * @param userId 발신자 ID
     * @param request 채팅 메시지 요청
     */
    @Override
    @Transactional
    public void sendMessage(Long userId, ChatMessageRequest request) {
        log.info("[ChatService] Start sending message from userId: {} in chatRoomId: {}", userId, request.getChatRoomId());

        // 1. 채팅방 유효성 검증
        ChatRoom chatRoom = chatRoomRepository.findById(request.getChatRoomId())
                .orElseThrow(() -> new CustomException(ResponseCode.MESSAGE_ROOM_NOT_FOUND));
        log.info("[ChatService] ChatRoom found: {}", chatRoom.getId());

        // 2. 발신자 유효성 검증
        User sender = userService.findById(userId)
                .orElseThrow(() -> new CustomException(ResponseCode.PROFILE_USER_NOT_FOUND));
        log.info("[ChatService] Sender found: {}", sender.getId());

        // 3. 사용자 권한 확인 (채팅방 멤버인지 확인)
        chatRoomMemberRepository.findByChatRoomAndUser_Id(chatRoom, userId)
                .orElseThrow(() -> new CustomException(ResponseCode.MESSAGE_ROOM_FORBIDDEN));
        log.info("[ChatService] Sender {} is a valid member of chatRoom {}", userId, chatRoom.getId());

        // 4. 메시지 엔티티 생성 및 저장
        Message message = chatMapper.toMessageEntity(chatRoom, sender, request);
        messageRepository.save(message);
        log.info("[ChatService] Message saved to DB with id: {}", message.getId());

        // 5. ChatMessage 생성 후 반환
        ChatMessage chatMessage = chatMapper.toChatMessage(message);
        if (message.getMessageType() == MessageType.IMAGE && message.getImageUrl() != null) {
            String presignedUrl = s3Service.generatePresignedUrlForThumbnail(message.getImageUrl());
            chatMessage.setImageUrl(presignedUrl);
        }
        log.info("[ChatService] Returning ChatMessage DTO: {}", chatMessage);

        // 6. WebSocket을 통해 메시지 전송
        webSocketMessageService.sendChatMessage(request.getChatRoomId(), chatMessage);

        // 7. 상대방에게 새 메시지 도착 이벤트 전송
        User recipient = chatRoom.getMembers().stream()
                .map(ChatRoomMember::getUser)
                .filter(user -> !user.getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new CustomException(ResponseCode.PROFILE_USER_NOT_FOUND)); // 채팅방에 상대방이 없는 경우

        EventDto eventDto = EventDto.builder()
                .eventType(EventType.NEW_MESSAGE_RECEIVED)
                .build();
        webSocketMessageService.sendEventToUser(recipient.getEmail(), eventDto);
    }

    /**
     * 채팅방 목록 조회
     * @param userId 사용자 ID
     * @param sortBy 정렬 기준
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return 채팅방 목록 정보
     */
    @Override
    public ChatRoomListData getChatRoomList(Long userId, ChatSortBy sortBy, Integer page, Integer size) {

        // 1. 사용자 조회
        User currentUser = userService.findById(userId)
                .orElseThrow(() -> new CustomException(ResponseCode.PROFILE_USER_NOT_FOUND));

        // 2. 사용자가 참여하고 있는 모든 채팅방 멤버 조회
        List<ChatRoomMember> chatRoomMembers = chatRoomMemberRepository.findByUserAndLeftAtIsNull(currentUser);

        List<ChatRoomInfo> chatRoomInfoList = new ArrayList<>();

        for (ChatRoomMember member : chatRoomMembers) {
            ChatRoom chatRoom = member.getChatRoom();

            // 3. 상대방 정보 조회
            User partner = chatRoom.getMembers().stream()
                    .map(ChatRoomMember::getUser)
                    .filter(user -> !user.getId().equals(userId))
                    .findFirst()
                    .orElseThrow(() -> new CustomException(ResponseCode.PROFILE_USER_NOT_FOUND));

            String partnerImageUrl = s3Service.generatePresignedUrlForThumbnail(partner.getProfileImageUrl());

            // 4. 마지막 메시지 조회
            Optional<Message> lastMessageOpt = messageRepository.findTopByChatRoomOrderByCreatedAtDesc(chatRoom);

            // 5. 읽지 않은 메시지 수 조회 (상대방이 보낸 메시지 중 내가 읽지 않은 것)
            Long unreadCount = messageRepository.countByChatRoomAndUserNotAndIsReadFalse(chatRoom, currentUser);

            // 6. ChatRoomInfo DTO 생성
            ChatRoomInfo chatRoomInfo = chatMapper.toChatRoomInfo(chatRoom, partner, partnerImageUrl, lastMessageOpt, unreadCount);
            chatRoomInfoList.add(chatRoomInfo);
        }

        // 7. 정렬
        if (sortBy == ChatSortBy.LATEST) {
            chatRoomInfoList.sort(Comparator.comparing(ChatRoomInfo::getLastMessageAt, Comparator.nullsLast(Comparator.reverseOrder())));
        } else if (sortBy == ChatSortBy.UNREAD) {
            chatRoomInfoList.sort(Comparator.comparing(ChatRoomInfo::getUnreadCount, Comparator.reverseOrder())
                    .thenComparing(ChatRoomInfo::getLastMessageAt, Comparator.nullsLast(Comparator.reverseOrder())));
        }

        // 8. 페이징
        int start = (int) PageRequest.of(page, size).getOffset();
        int end = Math.min((start + size), chatRoomInfoList.size());
        Page<ChatRoomInfo> chatRoomInfoPage = new PageImpl<>(chatRoomInfoList.subList(start, end), PageRequest.of(page, size), chatRoomInfoList.size());

        // 9. Pagination 정보 생성
        Pagination pagination = new Pagination(
                chatRoomInfoPage.getNumber(),
                chatRoomInfoPage.getSize(),
                chatRoomInfoPage.getTotalElements(),
                chatRoomInfoPage.getTotalPages(),
                chatRoomInfoPage.isLast()
        );

        return new ChatRoomListData(chatRoomInfoPage.getContent(), pagination);
    }

    /**
     * 채팅방 나가기
     * @param userId 사용자 ID
     * @param chatRoomId 채팅방 ID
     */
    @Override
    @Transactional
    public void leaveChatRoom(Long userId, Long chatRoomId) {

        // 1. 채팅방 조회
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new CustomException(ResponseCode.MESSAGE_ROOM_NOT_FOUND));

        // 2. 나가려는 사용자가 채팅방의 멤버이며, 아직 나가지 않은 상태인지 확인
        ChatRoomMember member = chatRoomMemberRepository.findByChatRoomAndUser_IdAndLeftAtIsNull(chatRoom, userId)
                .orElseThrow(() -> new CustomException(ResponseCode.MESSAGE_ROOM_FORBIDDEN));

        // 3. 나간 시간 업데이트하여 소프트 삭제 처리
        member.setLeftAt(LocalDateTime.now());
        chatRoomMemberRepository.save(member);
    }

    /**
     * 시스템 메시지 전송
     * @param chatRoomId 채팅방 ID
     * @param messageContent 메시지 내용 (프론트엔드에서 사용될 플레이스홀더 또는 실제 내용)
     * @param messageType 메시지 타입 (예: PROPOSAL)
     * @param exhibition 관련 전시
     * @param sender 발신자
     */
    @Override
    @Transactional
    public void sendSystemMessage(Long chatRoomId, String messageContent, MessageType messageType, Exhibition exhibition, User sender) {

        log.info("[ChatService] Start sending system message in chatRoomId: {}", chatRoomId);

        // 1. 채팅방 유효성 검증
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new CustomException(ResponseCode.MESSAGE_ROOM_NOT_FOUND));
        log.info("[ChatService] ChatRoom found: {}", chatRoom.getId());

        // 2. 메시지 엔티티 생성 및 저장
        Message message = Message.builder()
                .chatRoom(chatRoom)
                .user(sender)
                .messageType(messageType)
                .exhibition(exhibition)
                .content(messageContent)
                .build();
        messageRepository.save(message);
        log.info("[ChatService] System message saved to DB with id: {}", message.getId());

        // 3. ChatMessage 생성 후 반환
        ChatMessage chatMessage = chatMapper.toChatMessage(message, exhibition);
        log.info("[ChatService] Returning ChatMessage DTO: {}", chatMessage);

        // 4. WebSocket을 통해 메시지 전송
        webSocketMessageService.sendChatMessage(chatRoomId, chatMessage);

        // 5. 상대방에게 시스템 이벤트 알림 전송 (개인 알림 채널)
        User recipient = chatRoom.getMembers().stream()
                .map(ChatRoomMember::getUser)
                .filter(user -> !user.getId().equals(sender.getId()))
                .findFirst()
                .orElseThrow(() -> new CustomException(ResponseCode.PROFILE_USER_NOT_FOUND));

        EventDto eventDto = EventDto.builder()
                .eventType(EventType.NEW_MESSAGE_RECEIVED)
                .build();

        webSocketMessageService.sendEventToUser(recipient.getEmail(), eventDto);
    }
}