package io.shinmen.airnewsaggregator.payload.request.validator;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.springframework.beans.factory.annotation.Value;

import io.shinmen.airnewsaggregator.payload.request.EverythingQueryRequest;
import io.shinmen.airnewsaggregator.payload.request.validator.annotation.DateRange;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DateRangeValidator implements ConstraintValidator<DateRange, EverythingQueryRequest> {

    private static final String FORMAT_DATE = "yyyy-MM-dd";
    private static final String FORMAT_DATETIME = "yyyy-MM-dd'T'HH:mm:ss";

    @Value("${air-news-aggregator.app.default-timezone:UTC}")
    private String defaultTimeZone;

    @Override
    public boolean isValid(EverythingQueryRequest request, ConstraintValidatorContext context) {
        if (request.getFrom() == null || request.getTo() == null) {
            return true;
        }

        ZonedDateTime fromDate = parseDate(request.getFrom());
        ZonedDateTime toDate = parseDate(request.getTo());

        if (fromDate == null || toDate == null) {
            return false;
        }

        return !fromDate.isAfter(toDate);
    }

    private ZonedDateTime parseDate(String dateString) {
        try {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(FORMAT_DATE);
            LocalDate localDate = LocalDate.parse(dateString, dateFormatter);
            return localDate.atStartOfDay(ZoneId.of(defaultTimeZone));

        } catch (DateTimeParseException e) {
            log.debug("Date is not in 'yyyy-MM-dd' format, trying other formats: {}", dateString, e);
        }

        try {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(FORMAT_DATETIME);
            return ZonedDateTime.parse(dateString, dateTimeFormatter.withZone(ZoneId.of(defaultTimeZone)));

        } catch (DateTimeParseException e) {
            log.debug("Invalid date format for ValidZonedDateTime: {}", dateString, e);
        }

        return null;
    }
}
