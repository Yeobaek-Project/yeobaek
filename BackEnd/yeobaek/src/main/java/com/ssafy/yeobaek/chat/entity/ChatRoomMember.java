package com.ssafy.yeobaek.chat.entity;

import com.ssafy.yeobaek.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "chat_room_member", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "chat_room_id"})
})
public class ChatRoomMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    @Column(name = "last_read_at", nullable = false)
    @Builder.Default
    private LocalDateTime lastReadAt = LocalDateTime.now();

    @Column(name = "left_at")
    private LocalDateTime leftAt;
}
