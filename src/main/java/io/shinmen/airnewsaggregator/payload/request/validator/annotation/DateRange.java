package io.shinmen.airnewsaggregator.payload.request.validator.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.shinmen.airnewsaggregator.payload.request.validator.DateRangeValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = DateRangeValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface DateRange {
    String message() default "The 'from' date must be earlier than or equal to the 'to' date";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
