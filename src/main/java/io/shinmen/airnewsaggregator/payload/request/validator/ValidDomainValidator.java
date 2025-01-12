package io.shinmen.airnewsaggregator.payload.request.validator;

import java.util.regex.Pattern;

import io.shinmen.airnewsaggregator.payload.request.validator.annotation.ValidDomain;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidDomainValidator implements ConstraintValidator<ValidDomain, String> {

    private static final String DOMAIN_PATTERN = "^[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
    private final Pattern pattern = Pattern.compile(DOMAIN_PATTERN);

    @Override
    public boolean isValid(final String domains, final ConstraintValidatorContext context) {
        if (domains == null || domains.isEmpty()) {
            return true;
        }

        final String[] domainArray = domains.split(",");

        for (String domain : domainArray) {
            domain = domain.trim();
            if (!pattern.matcher(domain).matches()) {
                return false;
            }
        }

        return true;
    }
}
