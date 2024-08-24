package io.shinmen.airnewsaggregator.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.shinmen.airnewsaggregator.model.NewsPreference;
import io.shinmen.airnewsaggregator.payload.response.NewsPreferenceResponse;

@Configuration
public class ModelMapperConfig {

    @Bean
    ModelMapper modelMapper() {

        ModelMapper modelMapper = new ModelMapper();

        // Apply custom configurations for the ModelMapper
        configureModelMapper(modelMapper);

        // Add mappings for specific classes
        configureMappings(modelMapper);

        return modelMapper;
    }

    private void configureModelMapper(ModelMapper modelMapper) {

        // Setting the configuration separately from mapping logic
        org.modelmapper.config.Configuration config = modelMapper.getConfiguration();
        config.setFieldMatchingEnabled(true);
        config.setSkipNullEnabled(true);
        config.setMatchingStrategy(MatchingStrategies.LOOSE);
    }

    private void configureMappings(ModelMapper modelMapper) {

        // Custom mapping from NewsPreference to NewsPreferenceResponse
        modelMapper.typeMap(NewsPreference.class, NewsPreferenceResponse.class)
                .addMappings(mapper -> {
                    mapper.map(src -> src.getCountry() != null ? src.getCountry().toValue() : null,
                            NewsPreferenceResponse::setCountry);
                    mapper.map(src -> src.getLanguage() != null ? src.getLanguage().toValue() : null,
                            NewsPreferenceResponse::setLanguage);
                });
    }
}
