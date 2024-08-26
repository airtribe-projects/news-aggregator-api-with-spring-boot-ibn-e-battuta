package io.shinmen.airnewsaggregator.payload.request.validator;

import java.util.Set;

import io.shinmen.airnewsaggregator.payload.request.validator.annotation.Sources;
import io.shinmen.airnewsaggregator.payload.request.validator.helper.ValidationHelper;
import io.shinmen.airnewsaggregator.service.SourceService;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SourcesValidator implements ConstraintValidator<Sources, Set<String>> {

    private final SourceService sourceService;

    @Override
    public boolean isValid(Set<String> sourceIds, ConstraintValidatorContext context) {
        if (sourceIds == null || sourceIds.isEmpty()) {
            return true;
        }

        return ValidationHelper.getAllSourceIds(context, sourceIds, sourceService);
    }
}
