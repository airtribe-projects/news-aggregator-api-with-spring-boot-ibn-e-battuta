package io.shinmen.airnewsaggregator.payload.request.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;

import io.shinmen.airnewsaggregator.model.enums.Language;

public class StringToLanguageConverter implements Converter<String, Language> {
    @Override
    public Language convert(@NonNull String source) {
        return Language.fromValue(source);
    }
}
