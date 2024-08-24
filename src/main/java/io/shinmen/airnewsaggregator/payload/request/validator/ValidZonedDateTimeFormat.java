package io.shinmen.airnewsaggregator.payload.request.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = ValidZonedDateTimeFormatValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidZonedDateTimeFormat {
    String message() default "Invalid date format. Use 'yyyy-MM-dd' or 'yyyy-MM-ddTHH:mm:ss'";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
