package io.shinmen.airnewsaggregator.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.shinmen.airnewsaggregator.payload.request.converter.StringToCategoryConverter;
import io.shinmen.airnewsaggregator.payload.request.converter.StringToCountryConverter;
import io.shinmen.airnewsaggregator.payload.request.converter.StringToLanguageConverter;
import io.shinmen.airnewsaggregator.payload.request.converter.StringToSortByConverter;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(@NonNull FormatterRegistry registry) {
        registry.addConverter(new StringToCategoryConverter());
        registry.addConverter(new StringToCountryConverter());
        registry.addConverter(new StringToLanguageConverter());
        registry.addConverter(new StringToSortByConverter());
    }
}
