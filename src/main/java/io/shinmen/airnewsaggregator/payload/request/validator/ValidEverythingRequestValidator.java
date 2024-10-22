package io.shinmen.airnewsaggregator.payload.request.validator;

import io.shinmen.airnewsaggregator.payload.request.EverythingQueryRequest;
import io.shinmen.airnewsaggregator.payload.request.validator.annotation.ValidEverythingRequest;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ValidEverythingRequestValidator
        implements ConstraintValidator<ValidEverythingRequest, EverythingQueryRequest> {

    @Override
    public boolean isValid(final EverythingQueryRequest request, final ConstraintValidatorContext context) {
        return request.getQuery() != null || request.getSources() != null || request.getDomains() != null;
    }
}
