package com.ssafy.yeobaek.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "이메일 중복 검증 응답 데이터")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmailCheckData {
    @Schema(description = "확인한 이메일 주소", example = "user@example.com")
    private String email;

    @Schema(description = "사용 가능 여부 (true: 사용 가능, false: 중복)", example = "true")
    private Boolean available;
}
