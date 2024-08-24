package io.shinmen.airnewsaggregator.payload.request.validator;

import io.shinmen.airnewsaggregator.model.enums.Category;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidCategoryValidator implements ConstraintValidator<ValidCategory, Category> {

    @Override
    public boolean isValid(Category category, ConstraintValidatorContext context) {
        if (category == null) {
            return true;
        }

        return category != Category.UNKNOWN;
    }
}
