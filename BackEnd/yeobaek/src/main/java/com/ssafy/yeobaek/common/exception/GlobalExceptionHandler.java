package com.ssafy.yeobaek.common.exception;

import com.ssafy.yeobaek.common.code.ResponseCode;
import com.ssafy.yeobaek.common.dto.ResponseDto;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // CustomException 처리
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ResponseDto<Void>> handleCustomException(CustomException e) {
        ResponseCode responseCode = e.getResponseCode();
        log.warn("CustomException 발생: 코드 = {}, 메시지 = {}", responseCode.getCode(), responseCode.getMessage());
        return ResponseEntity
            .status(responseCode.getHttpStatus())
            .body(ResponseDto.error(responseCode));
    }

    // 권한 거부 예외 처리
    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ResponseDto<Void>> handleAuthorizationDeniedException(AuthorizationDeniedException e) {
        log.warn("권한 거부 (Forbidden): {}", e.getMessage());
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(ResponseDto.error(ResponseCode.FORBIDDEN));
    }

    // Validation 예외 처리 (@RequestBody + @Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDto<Void>> handleValidationException(MethodArgumentNotValidException e) {
        // 첫 번째 에러 메시지 추출
        FieldError fieldError = e.getBindingResult().getFieldErrors().get(0);
        String errorMessage = fieldError.getDefaultMessage();
        log.warn("Validation 예외 발생: 필드 = {}, 메시지 = {}", fieldError.getField(), errorMessage);

        // 에러 메시지에 따라 적절한 ResponseCode 매핑
        ResponseCode responseCode = ResponseCode.valueOf(errorMessage);

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ResponseDto.error(responseCode));
    }

    // Validation 예외 처리 (@RequestParam + @Validated)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ResponseDto<Void>> handleConstraintViolationException(ConstraintViolationException e) {
        // 첫 번째 에러 메시지 추출
        String errorMessage = e.getConstraintViolations().iterator().next().getMessage();
        log.warn("ConstraintViolation 예외 발생: 메시지 = {}", errorMessage);

        // 에러 메시지에 따라 적절한 ResponseCode 매핑
        ResponseCode responseCode = ResponseCode.valueOf(errorMessage);

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ResponseDto.error(responseCode));
    }

    // 기타 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto<Void>> handleException(Exception e) {
        log.error("처리되지 않은 예외 발생", e);
        return ResponseEntity
            .status(ResponseCode.INTERNAL_SERVER_ERROR.getHttpStatus())
            .body(ResponseDto.error(ResponseCode.INTERNAL_SERVER_ERROR));
    }
}
