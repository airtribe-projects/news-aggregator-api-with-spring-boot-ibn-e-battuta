package io.shinmen.airnewsaggregator.payload.request.validator;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.springframework.beans.factory.annotation.Value;

import io.shinmen.airnewsaggregator.payload.request.validator.annotation.ValidZonedDateTime;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ZonedDateTimeValidator implements ConstraintValidator<ValidZonedDateTime, String> {

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
                    LocalDate.parse(value, formatter);
                } else if (format.equals("yyyy-MM-dd'T'HH:mm:ss")) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
                    java.time.ZonedDateTime.parse(value, formatter.withZone(ZoneId.of(defaultTimeZone)));
                }
                return true;
            } catch (DateTimeParseException e) {
                log.debug("Invalid date format: {}", value, e);
            }
        }

        return false;
    }
}
