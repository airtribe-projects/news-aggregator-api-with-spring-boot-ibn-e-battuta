package io.shinmen.airnewsaggregator.payload.request.validator.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.shinmen.airnewsaggregator.payload.request.validator.SortByValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = SortByValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidSortBy {
    String message() default "Invalid sort by value.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
