package com.ssafy.yeobaek.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

public class ValidCategoryIdsValidator implements ConstraintValidator<ValidCategoryIds, List<Long>> {

    @Override
    public void initialize(ValidCategoryIds constraintAnnotation) {
        // 초기화 로직 (필요시)
    }

    @Override
    public boolean isValid(List<Long> categoryIds, ConstraintValidatorContext context) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return true; // @NotEmpty 어노테이션으로 별도 처리
        }

        for (Long id : categoryIds) {
            if (id == null || id < 1 || id > 8) {
                return false;
            }
        }
        return true;
    }
}
