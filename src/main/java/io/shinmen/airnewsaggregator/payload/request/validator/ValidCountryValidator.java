package io.shinmen.airnewsaggregator.payload.request.validator;

import io.shinmen.airnewsaggregator.model.enums.Country;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidCountryValidator implements ConstraintValidator<ValidCountry, Country> {

    @Override
    public boolean isValid(Country country, ConstraintValidatorContext context) {
        if (country == null) {
            return true;
        }

        return country != Country.UNKNOWN;
    }
}
