package io.shinmen.airnewsaggregator.payload.request.validator;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.springframework.beans.factory.annotation.Value;

import io.shinmen.airnewsaggregator.payload.request.EverythingQueryRequest;
import io.shinmen.airnewsaggregator.payload.request.validator.annotation.ValidDateRange;
import io.shinmen.airnewsaggregator.utility.Constants;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ValidDateRangeValidator implements ConstraintValidator<ValidDateRange, EverythingQueryRequest> {

    @Value("${air-news-aggregator.app.default-timezone:UTC}")
    private String defaultTimeZone;

    @Override
    public boolean isValid(final EverythingQueryRequest request, final ConstraintValidatorContext context) {
        if (request.getFrom() == null || request.getTo() == null) {
            return true;
        }

        final ZonedDateTime fromDate = parseDate(request.getFrom());
        final ZonedDateTime toDate = parseDate(request.getTo());

        if (fromDate == null || toDate == null) {
            return false;
        }

        return !fromDate.isAfter(toDate);
    }

    private ZonedDateTime parseDate(String date) {
        try {
            final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(Constants.FORMAT_DATE);
            final LocalDate localDate = LocalDate.parse(date, dateFormatter);
            return localDate.atStartOfDay(ZoneId.of(defaultTimeZone));

        } catch (DateTimeParseException e) {
            log.debug("Date is not in 'yyyy-MM-dd' format", date, e);
        }

        try {
            final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(Constants.FORMAT_DATETIME);
            return ZonedDateTime.parse(date, dateTimeFormatter.withZone(ZoneId.of(defaultTimeZone)));

        } catch (DateTimeParseException e) {
            log.debug("Date is not in 'yyyy-MM-ddTHH:mm:ss' format", date, e);
        }

        return null;
    }
}
