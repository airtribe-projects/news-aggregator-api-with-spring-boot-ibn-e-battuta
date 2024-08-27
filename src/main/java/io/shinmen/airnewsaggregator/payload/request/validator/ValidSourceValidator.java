package io.shinmen.airnewsaggregator.payload.request.validator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import io.shinmen.airnewsaggregator.payload.request.validator.annotation.ValidSource;
import io.shinmen.airnewsaggregator.payload.request.validator.helper.ValidationHelper;
import io.shinmen.airnewsaggregator.service.SourceService;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ValidSourceValidator implements ConstraintValidator<ValidSource, String> {

    private final SourceService sourceService;

    @Override
    public boolean isValid(final String value, final ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;
        }

        final String[] items = value.split(",");
        final Set<String> sourceIds = new HashSet<>(Arrays.asList(items));

        return ValidationHelper.getAllSourceIds(context, sourceIds, sourceService);
    }
}
