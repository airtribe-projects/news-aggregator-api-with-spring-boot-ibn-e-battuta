package io.shinmen.airnewsaggregator.payload.request.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import io.shinmen.airnewsaggregator.model.enums.Country;

@Component
public class StringToCountryConverter implements Converter<String, Country> {
    @Override
    public Country convert(@NonNull String source) {
        return Country.fromValue(source);
    }
}
