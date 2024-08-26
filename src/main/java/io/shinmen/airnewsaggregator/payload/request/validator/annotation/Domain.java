package io.shinmen.airnewsaggregator.payload.request.validator.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.shinmen.airnewsaggregator.payload.request.validator.DomainValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = DomainValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface Domain {
    String message() default "One or more sources are invalid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
