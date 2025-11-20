package com.ssafy.yeobaek.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ValidCategoryIdsValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCategoryIds {
    String message() default "CAFE_CATEGORY_NOT_FOUND";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
