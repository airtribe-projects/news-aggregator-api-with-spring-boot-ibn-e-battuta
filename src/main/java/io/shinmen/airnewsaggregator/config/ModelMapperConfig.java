package io.shinmen.airnewsaggregator.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.shinmen.airnewsaggregator.model.NewsPreference;
import io.shinmen.airnewsaggregator.payload.response.NewsPreferenceResponse;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setFieldMatchingEnabled(true).setSkipNullEnabled(true);

        modelMapper.typeMap(NewsPreference.class, NewsPreferenceResponse.class).addMappings(mapper -> {
            mapper.map(src -> src.getUser().getUsername(), NewsPreferenceResponse::setUsername);
            mapper.map(src -> src.getUser().getEmail(), NewsPreferenceResponse::setEmail);
            mapper.map(src -> src.getCountry() != null ? src.getCountry().toValue() : null, NewsPreferenceResponse::setCountry);
            mapper.map(src -> src.getLanguage() != null ? src.getLanguage().toValue() : null, NewsPreferenceResponse::setLanguage);
        });

        return modelMapper;
    }
}
