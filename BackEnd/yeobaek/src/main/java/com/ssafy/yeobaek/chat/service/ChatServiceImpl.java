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
    private final UserService userService;
    private final S3Service s3Service;
    private final WebSocketMessageService webSocketMessageService;
    private final ChatPresenceService chatPresenceService;

    /**
     * 채팅방 생성
     * @param userId 사용자 ID
     * @param partnerId 상대방 ID
     * @return 생성된 채팅방 정보
     */
    @Override
    @Transactional
    public ChatRoomData createChatRoom(Long userId, Long partnerId) {
        log.info("채팅방 생성 시작 - 사용자 ID: {}, 상대방 ID: {}", userId, partnerId);
        // 1. 유효성 검증
        if (userId.equals(partnerId)) {
            log.warn("채팅방 생성 실패 - 자신에게 채팅을 시도했습니다. 사용자 ID: {}", userId);
            throw new CustomException(ResponseCode.MESSAGE_SELF_SEND_NOT_ALLOWED);
        }

        // 2. 사용자 조회
        User user = userService.findById(userId)
                .orElseThrow(() -> {
                    log.warn("채팅방 생성 실패 - 존재하지 않는 사용자 ID: {}", userId);
                    return new CustomException(ResponseCode.PROFILE_USER_NOT_FOUND);
                });
        User partner = userService.findById(partnerId)
                .orElseThrow(() -> {
                    log.warn("채팅방 생성 실패 - 존재하지 않는 상대방 ID: {}", partnerId);
                    return new CustomException(ResponseCode.PROFILE_USER_NOT_FOUND);
                });

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
            log.info("기존 채팅방을 재사용합니다 - 채팅방 ID: {}", existingChatRoom.getId());

            // 현재 사용자의 ChatRoomMember를 찾아 leftAt을 null로 업데이트 (재참여)
            chatRoomMemberRepository.findByChatRoomAndUser_Id(existingChatRoom, userId)
                    .ifPresent(member -> {
                        if (member.getLeftAt() != null) {
                            log.info("사용자가 채팅방에 재입장합니다. leftAt을 null로 업데이트합니다. - 사용자 ID: {}, 채팅방 ID: {}", userId, existingChatRoom.getId());
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
            log.info("새로운 채팅방을 생성했습니다 - 채팅방 ID: {}", newChatRoom.getId());

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
            webSocketMessageService.sendEventToUser(partner.getId(), eventDto);
            log.info("상대방에게 채팅방 생성 이벤트를 전송했습니다 - 상대방 ID: {}", partner.getId());
        }

        log.info("채팅방 생성/조회 완료 - 채팅방 ID: {}", chatRoomData.getChatRoom().getChatRoomId());
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
    @Transactional
    public ChatRoomDetailData getChatRoomMessages(Long userId, Long chatRoomId, Integer page, Integer size) {
        log.info("채팅방 메시지 조회 시작 - 사용자 ID: {}, 채팅방 ID: {}, 페이지: {}, 사이즈: {}", userId, chatRoomId, page, size);
        // 1. 채팅방 조회
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> {
                    log.warn("채팅방 메시지 조회 실패 - 존재하지 않는 채팅방 ID: {}", chatRoomId);
                    return new CustomException(ResponseCode.MESSAGE_ROOM_NOT_FOUND);
                });

        // 2. 사용자 권한 확인 (채팅방 멤버인지 확인)
        chatRoomMemberRepository.findByChatRoomAndUser_Id(chatRoom, userId)
                .orElseThrow(() -> {
                    log.warn("채팅방 메시지 조회 실패 - 권한 없음. 사용자 ID: {}, 채팅방 ID: {}", userId, chatRoomId);
                    return new CustomException(ResponseCode.MESSAGE_ROOM_FORBIDDEN);
                });

        // 3. 해당 채팅방의 안 읽은 메시지를 모두 읽음 처리
        int updatedCount = messageRepository.updateReadStatusInBulk(chatRoomId, userId);
        if (updatedCount > 0) {
            log.info("{}개의 메시지를 읽음 처리했습니다 - 채팅방 ID: {}, 사용자 ID: {}", updatedCount, chatRoomId, userId);
        }

        // 4. 상대방 정보 조회
        User partner = chatRoom.getMembers().stream()
                .map(ChatRoomMember::getUser)
                .filter(user -> !user.getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("채팅방에 상대방이 존재하지 않습니다 - 채팅방 ID: {}", chatRoomId);
                    return new CustomException(ResponseCode.PROFILE_USER_NOT_FOUND);
                });

        String partnerImageUrl = s3Service.generatePresignedUrlForThumbnail(partner.getProfileImageUrl());

        // 4. 채팅방 요약 정보 생성
        ChatRoomSummary chatRoomSummary = chatMapper.toChatRoomSummary(chatRoom, partner, partnerImageUrl);

        // 5. 메시지 목록 조회 (페이지네이션 적용)
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Message> messagePage = messageRepository.findByChatRoomWithExhibition(chatRoom, pageable);

        // 6. DTO 변환 (S3 URL 변환 포함)
        List<ChatMessage> chatMessages = messagePage.getContent().stream()
                .map(message -> {
                    ChatMessage chatMessage = chatMapper.toChatMessage(message);
                    if (message.getMessageType() == MessageType.IMAGE && message.getImageUrl() != null) {
                        String presignedUrl = s3Service.generatePresignedUrlForDownload(message.getImageUrl());
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
                messagePage.hasNext()
        );

        log.info("채팅방 메시지 조회 완료 - 조회된 메시지 수: {}", chatMessages.size());
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
        log.info("채팅 메시지 전송 시작 - 발신자 ID: {}, 채팅방 ID: {}", userId, request.getChatRoomId());

        // 1. 채팅방 유효성 검증
        ChatRoom chatRoom = chatRoomRepository.findById(request.getChatRoomId())
                .orElseThrow(() -> {
                    log.warn("채팅 메시지 전송 실패 - 존재하지 않는 채팅방 ID: {}", request.getChatRoomId());
                    return new CustomException(ResponseCode.MESSAGE_ROOM_NOT_FOUND);
                });

        // 2. 발신자 유효성 검증
        User sender = userService.findById(userId)
                .orElseThrow(() -> {
                    log.warn("채팅 메시지 전송 실패 - 존재하지 않는 발신자 ID: {}", userId);
                    return new CustomException(ResponseCode.PROFILE_USER_NOT_FOUND);
                });

        // 3. 사용자 권한 확인 (채팅방 멤버인지 확인)
        chatRoomMemberRepository.findByChatRoomAndUser_Id(chatRoom, userId)
                .orElseThrow(() -> {
                    log.warn("채팅 메시지 전송 실패 - 사용자가 채팅방 멤버가 아님. 사용자 ID: {}, 채팅방 ID: {}", userId, chatRoom.getId());
                    return new CustomException(ResponseCode.MESSAGE_ROOM_FORBIDDEN);
                });

        // 4. 받는 사람 조회
        User recipient = chatRoom.getMembers().stream()
                .map(ChatRoomMember::getUser)
                .filter(user -> !user.getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("채팅방에 상대방이 존재하지 않습니다 - 채팅방 ID: {}", chatRoom.getId());
                    return new CustomException(ResponseCode.PROFILE_USER_NOT_FOUND);
                });

        // 5. 받는 사람이 현재 채팅방에 접속해 있는지 확인
        boolean isPresent = chatPresenceService.isUserPresent(request.getChatRoomId(), recipient.getId());

        // 6. 메시지 엔티티 생성 및 저장
        Message message = chatMapper.toMessageEntity(chatRoom, sender, request);
        message.setIsRead(isPresent);
        messageRepository.save(message);
        log.info("메시지 DB 저장 완료 - 메시지 ID: {}. 읽음 상태: {}", message.getId(), isPresent);

        // 7. ChatMessage 생성 후 반환
        ChatMessage chatMessage = chatMapper.toChatMessage(message);
        if (message.getMessageType() == MessageType.IMAGE && message.getImageUrl() != null) {
            String presignedUrl = s3Service.generatePresignedUrlForDownload(message.getImageUrl());
            chatMessage.setImageUrl(presignedUrl);
        }

        // 8. WebSocket을 통해 메시지 전송
        webSocketMessageService.sendChatMessage(request.getChatRoomId(), chatMessage);

        // 9. 상대방에게 새 메시지 도착 이벤트 전송
        EventDto eventDto = EventDto.builder()
                .eventType(EventType.NEW_MESSAGE_RECEIVED)
                .build();
        webSocketMessageService.sendEventToUser(recipient.getId(), eventDto);
        log.info("채팅 메시지 전송 완료 - 채팅방 ID: {}", request.getChatRoomId());
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
        log.info("채팅방 목록 조회 시작 - 사용자 ID: {}, 정렬 기준: {}, 페이지: {}, 사이즈: {}", userId, sortBy, page, size);
        // 1. 사용자 조회
        User currentUser = userService.findById(userId)
                .orElseThrow(() -> {
                    log.warn("채팅방 목록 조회 실패 - 존재하지 않는 사용자 ID: {}", userId);
                    return new CustomException(ResponseCode.PROFILE_USER_NOT_FOUND);
                });

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
                    .orElseThrow(() -> {
                        log.error("채팅방에 상대방이 존재하지 않습니다 - 채팅방 ID: {}", chatRoom.getId());
                        return new CustomException(ResponseCode.PROFILE_USER_NOT_FOUND);
                    });

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
                chatRoomInfoPage.hasNext()
        );

        log.info("채팅방 목록 조회 완료 - 조회된 채팅방 수: {}", chatRoomInfoPage.getNumberOfElements());
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
        log.info("채팅방 나가기 시작 - 사용자 ID: {}, 채팅방 ID: {}", userId, chatRoomId);
        // 1. 채팅방 조회
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> {
                    log.warn("채팅방 나가기 실패 - 존재하지 않는 채팅방 ID: {}", chatRoomId);
                    return new CustomException(ResponseCode.MESSAGE_ROOM_NOT_FOUND);
                });

        // 2. 나가려는 사용자가 채팅방의 멤버이며, 아직 나가지 않은 상태인지 확인
        ChatRoomMember member = chatRoomMemberRepository.findByChatRoomAndUser_IdAndLeftAtIsNull(chatRoom, userId)
                .orElseThrow(() -> {
                    log.warn("채팅방 나가기 실패 - 사용자가 멤버가 아니거나 이미 나간 상태입니다. 사용자 ID: {}, 채팅방 ID: {}", userId, chatRoomId);
                    return new CustomException(ResponseCode.MESSAGE_ROOM_FORBIDDEN);
                });

        // 3. 나간 시간 업데이트하여 소프트 삭제 처리
        member.setLeftAt(LocalDateTime.now());
        chatRoomMemberRepository.save(member);
        log.info("채팅방 나가기 완료 - 사용자 ID: {}, 채팅방 ID: {}", userId, chatRoomId);
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
        log.info("시스템 메시지 전송 시작 - 채팅방 ID: {}, 메시지 타입: {}", chatRoomId, messageType);

        // 1. 채팅방 유효성 검증
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> {
                    log.warn("시스템 메시지 전송 실패 - 존재하지 않는 채팅방 ID: {}", chatRoomId);
                    return new CustomException(ResponseCode.MESSAGE_ROOM_NOT_FOUND);
                });

        // 2. 메시지 엔티티 생성 및 저장
        Message message = Message.builder()
                .chatRoom(chatRoom)
                .user(sender)
                .messageType(messageType)
                .exhibition(exhibition)
                .content(messageContent)
                .build();

        // 발신자가 있는 경우에만 상대방의 접속 여부를 확인하여 isRead 설정
        if (sender != null) {
            User recipient = chatRoom.getMembers().stream()
                    .map(ChatRoomMember::getUser)
                    .filter(user -> !user.getId().equals(sender.getId()))
                    .findFirst()
                    .orElseThrow(() -> new CustomException(ResponseCode.PROFILE_USER_NOT_FOUND));
            boolean isPresent = chatPresenceService.isUserPresent(chatRoomId, recipient.getId());
            message.setIsRead(isPresent);
        } else {
            // 발신자가 없는 시스템 메시지(브로드캐스트)는 기본적으로 읽지 않은 상태로 생성
            message.setIsRead(false);
        }
        messageRepository.save(message);
        log.info("시스템 메시지 DB 저장 완료 - 메시지 ID: {}", message.getId());

        // 3. ChatMessage 생성 후 반환
        ChatMessage chatMessage = chatMapper.toChatMessage(message, exhibition);

        // 4. WebSocket을 통해 메시지 전송
        webSocketMessageService.sendChatMessage(chatRoomId, chatMessage);

        // 5. 상대방 또는 모든 멤버에게 시스템 이벤트 알림 전송
        EventDto eventDto = EventDto.builder()
                .eventType(EventType.NEW_MESSAGE_RECEIVED)
                .build();

        if (sender != null) {
            // 발신자가 있는 경우, 상대방에게만 알림
            User recipient = chatRoom.getMembers().stream()
                    .map(ChatRoomMember::getUser)
                    .filter(user -> !user.getId().equals(sender.getId()))
                    .findFirst()
                    .orElseThrow(() -> {
                        log.error("시스템 메시지 알림 전송 실패 - 채팅방에 상대방이 없습니다. 채팅방 ID: {}", chatRoomId);
                        return new CustomException(ResponseCode.PROFILE_USER_NOT_FOUND);
                    });
            webSocketMessageService.sendEventToUser(recipient.getId(), eventDto);
        } else {
            // 발신자가 없는 시스템 메시지의 경우, 모든 멤버에게 알림
            chatRoom.getMembers().forEach(member -> {
                webSocketMessageService.sendEventToUser(member.getUser().getId(), eventDto);
            });
        }
        log.info("시스템 메시지 전송 완료 - 채팅방 ID: {}", chatRoomId);
    }
}