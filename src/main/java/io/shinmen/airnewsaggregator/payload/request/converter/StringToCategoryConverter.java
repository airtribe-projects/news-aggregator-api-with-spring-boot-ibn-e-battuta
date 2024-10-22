package io.shinmen.airnewsaggregator.payload.request.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import io.shinmen.airnewsaggregator.model.enums.Category;

@Component
public class StringToCategoryConverter implements Converter<String, Category> {
    @Override
    public Category convert(@NonNull String source) {
        return Category.fromValue(source);
    }
}
