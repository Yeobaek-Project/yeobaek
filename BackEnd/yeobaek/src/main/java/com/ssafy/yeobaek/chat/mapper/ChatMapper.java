package com.ssafy.yeobaek.chat.mapper;

import com.ssafy.yeobaek.chat.dto.request.ChatMessageRequest;
import com.ssafy.yeobaek.chat.dto.response.*;
import com.ssafy.yeobaek.chat.entity.ChatRoom;
import com.ssafy.yeobaek.chat.entity.Message;
import com.ssafy.yeobaek.common.enums.MessageType;
import com.ssafy.yeobaek.exhibition.entity.Exhibition;
import com.ssafy.yeobaek.user.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class ChatMapper {

    /**
     * 채팅방 엔티티를 채팅방 정보 DTO로 변환 (새 채팅방)
     * @param chatRoom 채팅방 정보
     * @param partner 상대방 정보
     * @param partnerImageUrl 상대방 프로필 이미지 url
     * @return ChatRoomData dto
     */
    public ChatRoomData toChatRoomData(ChatRoom chatRoom, User partner, String partnerImageUrl) {
        return new ChatRoomData(toChatRoomSummary(chatRoom, partner, partnerImageUrl));
    }

    /**
     * 채팅방 엔티티와 파트너 정보를 ChatRoomSummary DTO로 변환
     * @param chatRoom 채팅방 엔티티
     * @param partner 상대방 User 엔티티
     * @param partnerImageUrl 상대방 프로필 이미지 URL
     * @return ChatRoomSummary DTO
     */
    public ChatRoomSummary toChatRoomSummary(ChatRoom chatRoom, User partner, String partnerImageUrl) {
        return ChatRoomSummary.builder()
                .chatRoomId(chatRoom.getId())
                .partnerId(partner.getId())
                .partnerName(partner.getNickname())
                .partnerImageUrl(partnerImageUrl)
                .partnerRole(partner.getUserType())
                .build();
    }

    /**
     * 채팅방 목록의 각 항목 DTO로 변환
     * @param chatRoom 채팅방 엔티티
     * @param partner 상대방 User 엔티티
     * @param partnerImageUrl 상대방 프로필 이미지 URL
     * @param lastMessageOpt 마지막 메시지
     * @param unreadCount 읽지 않은 메시지 수
     * @return ChatRoomInfo DTO
     */
    public ChatRoomInfo toChatRoomInfo(ChatRoom chatRoom, User partner, String partnerImageUrl, Optional<Message> lastMessageOpt, Long unreadCount) {
        return ChatRoomInfo.builder()
                .chatRoomId(chatRoom.getId())
                .partnerId(partner.getId())
                .partnerName(partner.getNickname())
                .partnerImageUrl(partnerImageUrl)
                .partnerRole(partner.getUserType())
                .unreadCount(unreadCount)
                .lastMessage(lastMessageOpt.map(Message::getContent).orElse(null))
                .lastMessageAt(lastMessageOpt.map(Message::getCreatedAt).orElse(null))
                .lastMessageType(lastMessageOpt.map(Message::getMessageType).orElse(null))
                .build();
    }

    /**
     * 새 메시지 도착 시 클라이언트에 전송될 채팅방 업데이트 요약 DTO로 변환
     * @param chatRoom 채팅방 엔티티
     * @param message 마지막 메시지 엔티티
     * @param unreadCount 읽지 않은 메시지 수
     * @return ChatRoomUpdateSummary DTO
     */
    public ChatRoomUpdateSummary toChatRoomUpdateSummary(ChatRoom chatRoom, Message message, Long unreadCount) {
        return ChatRoomUpdateSummary.builder()
                .chatRoomId(chatRoom.getId())
                .unreadCount(unreadCount)
                .lastMessage(message.getContent())
                .lastMessageType(message.getMessageType())
                .lastMessageAt(message.getCreatedAt())
                .build();
    }

    /**
     * Message 엔티티를 ChatMessage DTO로 변환 (Exhibition 정보 포함)
     * @param message Message 엔티티
     * @param exhibition Exhibition 엔티티 (null 가능)
     * @return ChatMessage DTO
     */
    public ChatMessage toChatMessage(Message message, Exhibition exhibition) {
        ExhibitionInfo exhibitionInfo = null;
        if (exhibition != null && (message.getMessageType() == MessageType.AGREED || message.getMessageType() == MessageType.STARTED || message.getMessageType() == MessageType.ENDED)) {
            exhibitionInfo = toExhibitionInfo(exhibition);
        }

        return ChatMessage.builder()
                .messageId(message.getId())
                .senderId(message.getUser() != null ? message.getUser().getId() : null)
                .messageType(message.getMessageType())
                .content(message.getContent())
                .imageUrl(message.getImageUrl())
                .exhibitionId(exhibition != null ? exhibition.getId() : null)
                .exhibition(exhibitionInfo)
                .isRead(message.getIsRead())
                .createdAt(message.getCreatedAt())
                .build();
    }

    /**
     * Message 엔티티를 ChatMessage DTO로 변환 (위임 메서드)
     * @param message Message 엔티티
     * @return ChatMessage DTO
     */
    public ChatMessage toChatMessage(Message message) {
        // LAZY 로딩된 Exhibition 객체를 그대로 전달
        return toChatMessage(message, message.getExhibition());
    }

    /**
     * 메시지 전송 요청 정보를 Message 엔티티로 변환
     * @param chatRoom 채팅방 정보
     * @param sender 발신자 정보
     * @param request 메시지 전송 요청 정보
     * @return Message 엔티티
     */
    public Message toMessageEntity(ChatRoom chatRoom, User sender, ChatMessageRequest request) {
        return Message.builder()
                .chatRoom(chatRoom)
                .user(sender)
                .content(request.getContent())
                .imageUrl(request.getImageUrl())
                .messageType(request.getMessageType())
                .build();
    }

    /**
     * Exhibition 엔티티를 ExhibitionInfo DTO로 변환
     * @param exhibition Exhibition 엔티티
     * @return ExhibitionInfo DTO
     */
    public ExhibitionInfo toExhibitionInfo(Exhibition exhibition) {
        return new ExhibitionInfo(
                exhibition.getId(),
                exhibition.getCafe().getCafeName(),
                exhibition.getDisplaySlot().getWall().getWallName(),
                exhibition.getArtwork().getTitle(),
                exhibition.getStartDate().toString(),
                exhibition.getEndDate().toString(),
                exhibition.getCommissionRate(),
                exhibition.getRequirements()
        );
    }
}