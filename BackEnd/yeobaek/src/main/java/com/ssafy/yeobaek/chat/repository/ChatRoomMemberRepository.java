package com.ssafy.yeobaek.chat.repository;

import com.ssafy.yeobaek.chat.entity.ChatRoom;
import com.ssafy.yeobaek.chat.entity.ChatRoomMember;
import com.ssafy.yeobaek.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {
    List<ChatRoomMember> findByUser(User user);
    List<ChatRoomMember> findByUserAndLeftAtIsNull(User user);
    Optional<ChatRoomMember> findByChatRoomAndUser_Id(ChatRoom chatRoom, Long userId);
    Optional<ChatRoomMember> findByChatRoomAndUser_IdAndLeftAtIsNull(ChatRoom chatRoom, Long userId);
}
