package com.ssafy.yeobaek.auth.dto.request;

import com.ssafy.yeobaek.common.enums.Gender;
import com.ssafy.yeobaek.common.enums.UserRole;
import com.ssafy.yeobaek.common.validation.ValidEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "회원가입 요청")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {
    @Schema(description = "회원 유형 (CAFE_OWNER, ARTIST, GENERAL)", example = "GENERAL", required = true)
    @NotBlank(message = "MISSING_REQUIRED_FIELD")
    @ValidEnum(enumClass = UserRole.class, message = "INVALID_USER_TYPE")
    private String userType;

    @Schema(description = "로그인용 이메일 주소", example = "user@example.com", required = true)
    @NotBlank(message = "MISSING_REQUIRED_FIELD")
    @Email(message = "INVALID_EMAIL_FORMAT")
    private String email;

    @Schema(description = "비밀번호 (8자 이상, 영문+숫자+특수문자)", example = "Password123!", required = true)
    @NotBlank(message = "MISSING_REQUIRED_FIELD")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
             message = "INVALID_PASSWORD_FORMAT")
    private String password;

    @Schema(description = "비밀번호 확인", example = "Password123!", required = true)
    @NotBlank(message = "MISSING_REQUIRED_FIELD")
    private String passwordConfirm;

    @Schema(description = "닉네임 (카페 사장: 카페 상호명, 작가: 작가명, 일반: 닉네임)", example = "홍길동", required = true)
    @NotBlank(message = "MISSING_REQUIRED_FIELD")
    @Size(min = 2, max = 15, message = "INVALID_NICKNAME_LENGTH")
    private String nickname;

    @Schema(description = "생년월일 (YYYY-MM-DD 형식)", example = "1990-01-01", required = true)
    @NotBlank(message = "MISSING_REQUIRED_FIELD")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "INVALID_BIRTH_DATE_FORMAT")
    private String birthDate;

    @Schema(description = "성별 (MALE, FEMALE)", example = "MALE", required = true)
    @NotBlank(message = "MISSING_REQUIRED_FIELD")
    @ValidEnum(enumClass = Gender.class, message = "INVALID_GENDER")
    private String gender;
}
