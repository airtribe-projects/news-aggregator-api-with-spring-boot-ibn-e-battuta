package io.shinmen.airnewsaggregator.payload.request.validator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import io.shinmen.airnewsaggregator.payload.request.validator.annotation.Source;
import io.shinmen.airnewsaggregator.payload.request.validator.helper.ValidationHelper;
import io.shinmen.airnewsaggregator.service.SourceService;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SourceValidator implements ConstraintValidator<Source, String> {

    private final SourceService sourceService;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;
        }

        String[] items = value.split(",");
        Set<String> sourceIds = new HashSet<>(Arrays.asList(items));

        return ValidationHelper.getAllSourceIds(context, sourceIds, sourceService);
    }
}
