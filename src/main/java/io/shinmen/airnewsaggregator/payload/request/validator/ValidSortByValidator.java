package io.shinmen.airnewsaggregator.payload.request.validator;

import io.shinmen.airnewsaggregator.payload.request.enums.SortBy;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidSortByValidator implements ConstraintValidator<ValidSortBy, SortBy> {

    @Override
    public boolean isValid(SortBy sortBy, ConstraintValidatorContext context) {
        if (sortBy == null) {
            return true;
        }

        return sortBy != SortBy.UNKNOWN;
    }
}
