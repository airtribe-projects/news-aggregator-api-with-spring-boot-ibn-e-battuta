package io.shinmen.airnewsaggregator.payload.request.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import io.shinmen.airnewsaggregator.payload.request.enums.SortBy;

@Component
public class StringToSortByConverter implements Converter<String, SortBy> {
    @Override
    public SortBy convert(@NonNull String source) {
        return SortBy.fromValue(source);
    }
}
