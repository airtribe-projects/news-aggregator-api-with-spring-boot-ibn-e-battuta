package io.shinmen.airnewsaggregator.payload.request.validator;

import io.shinmen.airnewsaggregator.model.enums.Country;
import io.shinmen.airnewsaggregator.payload.request.validator.annotation.ValidCountry;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CountryValidator implements ConstraintValidator<ValidCountry, Country> {

    @Override
    public boolean isValid(Country country, ConstraintValidatorContext context) {
        if (country == null) {
            return true;
        }

        return country != Country.UNKNOWN;
    }
}
