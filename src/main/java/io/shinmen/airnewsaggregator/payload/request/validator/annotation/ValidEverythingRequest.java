package io.shinmen.airnewsaggregator.payload.request.validator.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.shinmen.airnewsaggregator.payload.request.validator.ValidEverythingRequestValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = ValidEverythingRequestValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidEverythingRequest {
    String message() default "Required parameters are missing, the scope of search is too broad. "
            + "Please set any of the following required parameters and try again: query, sources, domains.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
