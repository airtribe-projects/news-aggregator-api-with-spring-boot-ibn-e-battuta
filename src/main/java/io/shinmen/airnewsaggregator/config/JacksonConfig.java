package io.shinmen.airnewsaggregator.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@Configuration
public class JacksonConfig {

    @Bean
    ObjectMapper objectMapper() {

        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.findAndRegisterModules();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return objectMapper;
    }
}
