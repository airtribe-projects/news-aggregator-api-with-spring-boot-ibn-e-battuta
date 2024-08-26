package io.shinmen.airnewsaggregator.payload.request.validator;

import io.shinmen.airnewsaggregator.payload.request.enums.SortBy;
import io.shinmen.airnewsaggregator.payload.request.validator.annotation.ValidSortBy;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SortByValidator implements ConstraintValidator<ValidSortBy, SortBy> {

    @Override
    public boolean isValid(SortBy sortBy, ConstraintValidatorContext context) {
        if (sortBy == null) {
            return true;
        }

        return sortBy != SortBy.UNKNOWN;
    }
}
