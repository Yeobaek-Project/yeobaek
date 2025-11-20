package com.ssafy.yeobaek.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ssafy.yeobaek.common.code.ResponseCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "공통 응답 DTO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDto<T> {
    @Schema(description = "응답 코드", example = "S0000")
    private String code;

    @Schema(description = "응답 데이터")
    private T data;

    @Schema(description = "응답 메시지", example = "정상 처리")
    private String message;

    // 성공 응답 - 데이터 포함
    public static <T> ResponseDto<T> success(T data) {
        return new ResponseDto<>(
            ResponseCode.SUCCESS.getCode(),
            data,
            ResponseCode.SUCCESS.getMessage()
        );
    }

    // 성공 응답 - 데이터 없음
    public static <T> ResponseDto<T> success() {
        return new ResponseDto<>(
            ResponseCode.SUCCESS.getCode(),
            null,
            ResponseCode.SUCCESS.getMessage()
        );
    }

    // 생성 성공 응답 - 데이터 포함 (201)
    public static <T> ResponseDto<T> created(T data) {
        return new ResponseDto<>(
                ResponseCode.CREATED.getCode(),
                data,
                ResponseCode.CREATED.getMessage()
        );
    }

    // 생성 성공 응답 - 데이터 없음 (201)
    public static <T> ResponseDto<T> created() {
        return new ResponseDto<>(
            ResponseCode.CREATED.getCode(),
            null,
            ResponseCode.CREATED.getMessage()
        );
    }

    // 에러 응답
    public static <T> ResponseDto<T> error(ResponseCode responseCode) {
        return new ResponseDto<>(
            responseCode.getCode(),
            null,
            responseCode.getMessage()
        );
    }
}
