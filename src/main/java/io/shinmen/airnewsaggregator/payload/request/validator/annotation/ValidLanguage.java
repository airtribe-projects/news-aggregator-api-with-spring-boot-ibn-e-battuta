package io.shinmen.airnewsaggregator.payload.request.validator.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.shinmen.airnewsaggregator.payload.request.validator.LanguageValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = LanguageValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidLanguage {
    String message() default "Invalid language value.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
