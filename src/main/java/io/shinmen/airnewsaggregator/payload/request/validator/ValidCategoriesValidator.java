package io.shinmen.airnewsaggregator.payload.request.validator;

import java.util.Set;

import io.shinmen.airnewsaggregator.model.enums.Category;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidCategoriesValidator implements ConstraintValidator<ValidCategories, Set<Category>> {

    @Override
    public boolean isValid(Set<Category> categories, ConstraintValidatorContext context) {
        if (categories == null || categories.isEmpty()) {
            return true;
        }

        for (Category category : categories) {
            if (category == null || !isValidCategory(category)) {
                return false;
            }
        }

        return true;
    }

    private boolean isValidCategory(Category category) {
        return category == Category.BUSINESS
                || category == Category.ENTERTAINMENT
                || category == Category.GENERAL
                || category == Category.HEALTH
                || category == Category.SCIENCE
                || category == Category.SPORTS
                || category == Category.TECHNOLOGY;
    }
}
