package io.shinmen.airnewsaggregator.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@Configuration
public class JacksonConfig {

    /**
     * Configures the ObjectMapper bean, ensuring that the JavaTimeModule is
     * registered
     * without duplication and disabling writing dates as timestamps.
     *
     * @return the configured ObjectMapper
     */
    @Bean
    ObjectMapper objectMapper() {

        ObjectMapper objectMapper = new ObjectMapper();

        // Automatically register all available modules, including JavaTimeModule
        objectMapper.findAndRegisterModules();

        // Disable writing dates as timestamps (ISO-8601 instead of UNIX timestamps)
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return objectMapper;
    }
}
