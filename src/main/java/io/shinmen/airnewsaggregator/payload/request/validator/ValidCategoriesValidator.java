package io.shinmen.airnewsaggregator.payload.request.validator;

import java.util.Set;

import io.shinmen.airnewsaggregator.model.enums.Category;
import io.shinmen.airnewsaggregator.payload.request.validator.annotation.ValidCategories;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidCategoriesValidator implements ConstraintValidator<ValidCategories, Set<Category>> {

    @Override
    public boolean isValid(final Set<Category> categories, final ConstraintValidatorContext context) {
        if (categories == null || categories.isEmpty()) {
            return true;
        }

        for (final Category category : categories) {
            if (category == Category.UNKNOWN) {
                return false;
            }
        }

        return true;
    }
}
