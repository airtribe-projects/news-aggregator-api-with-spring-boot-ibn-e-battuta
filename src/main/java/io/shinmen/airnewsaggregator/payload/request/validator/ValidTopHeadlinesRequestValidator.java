package io.shinmen.airnewsaggregator.payload.request.validator;

import io.shinmen.airnewsaggregator.payload.request.TopHeadLinesQueryRequest;
import io.shinmen.airnewsaggregator.payload.request.validator.annotation.ValidEverythingRequest;
import io.shinmen.airnewsaggregator.payload.request.validator.annotation.ValidTopHeadlinesRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ValidTopHeadlinesRequestValidator
        implements ConstraintValidator<ValidTopHeadlinesRequest, TopHeadLinesQueryRequest> {

    @Override
    public boolean isValid(final TopHeadLinesQueryRequest request, final ConstraintValidatorContext context) {

        if (request.getQuery() == null && request.getSources() == null && request.getCategory() == null
                && request.getCountry() == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "Required parameters are missing. " +
                            "Please set any of the following parameters and try again: sources, query, country, category.")
                    .addConstraintViolation();
            return false;
        }

        if (request.getSources() != null && (request.getCountry() != null || request.getCategory() != null)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "The 'sources' parameter cannot be used with 'country' or 'category'.")
                    .addConstraintViolation();
            return false;
        } else if (request.getCountry() != null && request.getSources() != null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "The 'country' parameter cannot be used with 'sources'.")
                    .addConstraintViolation();
            return false;
        } else if (request.getCategory() != null && request.getSources() != null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "The 'category' parameter cannot be used with 'sources'.")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
