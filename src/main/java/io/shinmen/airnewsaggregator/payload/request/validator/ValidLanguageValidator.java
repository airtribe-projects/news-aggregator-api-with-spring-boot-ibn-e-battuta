package io.shinmen.airnewsaggregator.payload.request.validator;

import io.shinmen.airnewsaggregator.model.enums.Language;
import io.shinmen.airnewsaggregator.payload.request.validator.annotation.ValidLanguage;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidLanguageValidator
        implements ConstraintValidator<ValidLanguage, Language> {

    @Override
    public boolean isValid(final Language language, final ConstraintValidatorContext context) {
        if (language == null) {
            return true;
        }

        return language != Language.UNKNOWN;
    }
}
