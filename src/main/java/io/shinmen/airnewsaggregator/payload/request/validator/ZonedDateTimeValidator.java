package io.shinmen.airnewsaggregator.payload.request.validator;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.springframework.beans.factory.annotation.Value;

import io.shinmen.airnewsaggregator.payload.request.validator.annotation.ValidZonedDateTime;
import io.shinmen.airnewsaggregator.utility.Constants;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ZonedDateTimeValidator implements ConstraintValidator<ValidZonedDateTime, String> {

    @Value("${air-news-aggregator.app.default-timezone:UTC}")
    private String defaultTimeZone;

    @Override
    public boolean isValid(final String value, final ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;
        }

        final String[] acceptableFormats = { Constants.FORMAT_DATE, Constants.FORMAT_DATETIME };

        for (final String format : acceptableFormats) {
            try {
                if (format.equals(Constants.FORMAT_DATE)) {
                    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
                    LocalDate.parse(value, formatter);
                } else if (format.equals(Constants.FORMAT_DATETIME)) {
                    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
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
