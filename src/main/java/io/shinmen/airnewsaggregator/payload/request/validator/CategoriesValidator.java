package io.shinmen.airnewsaggregator.payload.request.validator;

import java.util.Set;

import io.shinmen.airnewsaggregator.model.enums.Category;
import io.shinmen.airnewsaggregator.payload.request.validator.annotation.Categories;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CategoriesValidator implements ConstraintValidator<Categories, Set<Category>> {

    @Override
    public boolean isValid(Set<Category> categories, ConstraintValidatorContext context) {
        if (categories == null || categories.isEmpty()) {
            return true;
        }

        for (Category category : categories) {
            if (category == Category.UNKNOWN) {
                return false;
            }
        }

        return true;
    }
}
