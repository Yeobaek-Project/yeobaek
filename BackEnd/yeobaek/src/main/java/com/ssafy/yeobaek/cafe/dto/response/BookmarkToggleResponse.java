package com.ssafy.yeobaek.cafe.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "북마크 토글 응답 데이터")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookmarkToggleResponse {
    @Schema(description = "북마크 상태 (true: 추가됨, false: 제거됨)", example = "true")
    private Boolean isBookmarked;
}
