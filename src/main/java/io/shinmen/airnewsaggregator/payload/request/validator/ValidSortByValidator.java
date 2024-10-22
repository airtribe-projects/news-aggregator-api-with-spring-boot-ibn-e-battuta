package io.shinmen.airnewsaggregator.payload.request.validator;

import io.shinmen.airnewsaggregator.payload.request.enums.SortBy;
import io.shinmen.airnewsaggregator.payload.request.validator.annotation.ValidSortBy;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidSortByValidator implements ConstraintValidator<ValidSortBy, SortBy> {

    @Override
    public boolean isValid(final SortBy sortBy, final ConstraintValidatorContext context) {
        if (sortBy == null) {
            return true;
        }

        return sortBy != SortBy.UNKNOWN;
    }
}
