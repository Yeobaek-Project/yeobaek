package com.ssafy.yeobaek.chat.dto.response;

import com.ssafy.yeobaek.common.dto.Pagination;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Schema(description = "채팅방 이력 조회 응답")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomDetailData {

    @Schema(description = "채팅방 기본 정보")
    private ChatRoomSummary chatRoom;

    @Schema(description = "메시지 목록")
    private List<ChatMessage> messages;

    @Schema(description = "페이징 정보")
    private Pagination pagination;
}
