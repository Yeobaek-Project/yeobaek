package com.ssafy.yeobaek.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enum 값 검증을 위한 커스텀 어노테이션
 * 사용 예시: @ValidEnum(enumClass = UserRole.class, message = "INVALID_USER_TYPE")
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EnumValidator.class)
public @interface ValidEnum {

    String message() default "INVALID_ENUM_VALUE";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * 검증할 Enum 클래스
     */
    Class<? extends Enum<?>> enumClass();
}
