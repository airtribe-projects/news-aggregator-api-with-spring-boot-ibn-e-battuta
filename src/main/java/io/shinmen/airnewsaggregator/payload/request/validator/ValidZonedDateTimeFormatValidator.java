package io.shinmen.airnewsaggregator.payload.request.validator;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.springframework.beans.factory.annotation.Value;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ValidZonedDateTimeFormatValidator implements ConstraintValidator<ValidZonedDateTimeFormat, String> {

    @Value("${air-news-aggregator.app.default-timezone:UTC}")
    private String defaultTimeZone;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;
        }

        String[] acceptableFormats = { "yyyy-MM-dd", "yyyy-MM-dd'T'HH:mm:ss" };

        for (String format : acceptableFormats) {
            try {
                if (format.equals("yyyy-MM-dd")) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
                    formatter.parse(value);
                } else if (format.equals("yyyy-MM-dd'T'HH:mm:ss")) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
                    ZonedDateTime.parse(value, formatter.withZone(ZoneId.of(defaultTimeZone)));
                }
                return true;
            } catch (DateTimeParseException e) {
                log.debug("Invalid date format: {}", value, e);
            }
        }

        return false;
    }
}