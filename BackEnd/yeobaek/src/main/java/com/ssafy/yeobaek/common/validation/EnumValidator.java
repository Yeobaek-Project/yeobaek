package com.ssafy.yeobaek.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

/**
 * Enum 값 검증 Validator
 */
public class EnumValidator implements ConstraintValidator<ValidEnum, String> {

    private Class<? extends Enum<?>> enumClass;

    @Override
    public void initialize(ValidEnum annotation) {
        this.enumClass = annotation.enumClass();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // null 값은 @NotNull, @NotBlank 등 다른 어노테이션에서 처리
        if (value == null) {
            return true;
        }

        // Enum 상수 중에 일치하는 값이 있는지 확인
        return Arrays.stream(enumClass.getEnumConstants())
                .anyMatch(e -> e.name().equals(value));
    }
}
