package com.ssafy.yeobaek.chat.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "채팅방 생성 요청")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateChatRoomRequest {

    @NotNull(message = "MISSING_REQUIRED_FIELD")
    @Schema(description = "상대방 id", example = "1")
    private Long partnerId;
}
