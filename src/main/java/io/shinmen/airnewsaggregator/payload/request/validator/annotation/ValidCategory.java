package io.shinmen.airnewsaggregator.payload.request.validator.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.shinmen.airnewsaggregator.payload.request.validator.CategoryValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = CategoryValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCategory {
    String message() default "Invalid category value.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
