package io.shinmen.airnewsaggregator.payload.request.validator.helper;

import java.util.HashSet;
import java.util.Set;

import io.shinmen.airnewsaggregator.service.SourceService;

import jakarta.validation.ConstraintValidatorContext;

public class ValidationHelper {

    private ValidationHelper() {
    }

    public static boolean getAllSourceIds(final ConstraintValidatorContext context, final Set<String> sourceIds,
            final SourceService sourceService) {
        final Set<String> sources = sourceService.getAllSources();
        final Set<String> invalidSources = new HashSet<>(sourceIds);
        invalidSources.removeAll(sources);

        if (!invalidSources.isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "The following sources are invalid: " + String.join(", ", invalidSources))
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
