package io.shinmen.airnewsaggregator.payload.request.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = ValidLanguageValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidLanguage {
    String message() default "Invalid language value.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
