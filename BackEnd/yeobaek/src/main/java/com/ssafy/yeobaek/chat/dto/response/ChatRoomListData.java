package com.ssafy.yeobaek.chat.dto.response;

import com.ssafy.yeobaek.common.dto.Pagination;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Schema(description = "채팅방 목록 응답")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomListData {

    @Schema(description = "채팅방 정보")
    private List<ChatRoomInfo> chatRoom;

    @Schema(description = "페이징 정보")
    private Pagination pagination;
}
