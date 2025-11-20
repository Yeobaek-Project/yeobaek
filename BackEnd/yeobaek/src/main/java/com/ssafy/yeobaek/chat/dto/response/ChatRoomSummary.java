package com.ssafy.yeobaek.chat.dto.response;

import com.ssafy.yeobaek.common.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "채팅방 기본 정보")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomSummary {

    @Schema(description = "채팅방 ID", example = "1")
    private Long chatRoomId;

    @Schema(description = "상대방 ID", example = "1")
    private Long partnerId;

    @Schema(description = "상대방 이름 (작가명 또는 카페명)", example = "홍길동")
    private String partnerName;

    @Schema(description = "상대방 프로필 이미지 url", example = "https://example.com/image1.jpg")
    private String partnerImageUrl;

    @Schema(description = "상대방 역할", example = "ARTIST")
    private UserRole partnerRole;
}
