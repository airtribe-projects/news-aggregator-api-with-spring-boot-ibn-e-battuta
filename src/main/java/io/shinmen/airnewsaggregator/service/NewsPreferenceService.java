package io.shinmen.airnewsaggregator.service;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.shinmen.airnewsaggregator.exception.NewsPreferencesNotFoundException;
import io.shinmen.airnewsaggregator.model.NewsPreference;
import io.shinmen.airnewsaggregator.model.User;
import io.shinmen.airnewsaggregator.model.enums.Country;
import io.shinmen.airnewsaggregator.model.enums.Language;
import io.shinmen.airnewsaggregator.payload.request.NewsPreferenceUpdateRequest;
import io.shinmen.airnewsaggregator.payload.response.NewsPreferenceResponse;
import io.shinmen.airnewsaggregator.repository.NewsPreferenceRepository;
import io.shinmen.airnewsaggregator.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NewsPreferenceService {

    private final NewsPreferenceRepository newsPreferenceRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public NewsPreferenceResponse getPreferencesForUser(String username) {

        log.debug("Fetching preferences for user: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        NewsPreference newsPreference = newsPreferenceRepository.findByUser(user)
                .orElseThrow(() -> new NewsPreferencesNotFoundException("User preferences not found"));

        return convertToNewsPreferenceResponse(newsPreference);
    }

    @Transactional
    public NewsPreferenceResponse updatePreferences(String username,
            NewsPreferenceUpdateRequest updatedPreferences) {

        log.debug("Updating preferences for user: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        NewsPreference existingPreferences = newsPreferenceRepository.findByUser(user)
                .orElse(new NewsPreference());

        existingPreferences.setUser(user);

        Optional.ofNullable(updatedPreferences.getCategories()).ifPresent(existingPreferences::setCategories);
        Optional.ofNullable(updatedPreferences.getSources()).ifPresent(existingPreferences::setSources);
        Optional.ofNullable(updatedPreferences.getCountry())
                .ifPresent(country -> existingPreferences
                        .setCountry(Country.valueOf(country.toUpperCase())));
        Optional.ofNullable(updatedPreferences.getLanguage())
                .ifPresent(language -> existingPreferences
                        .setLanguage(Language.valueOf(language.toUpperCase())));

        newsPreferenceRepository.save(existingPreferences);
        return convertToNewsPreferenceResponse(existingPreferences);
    }

    private NewsPreferenceResponse convertToNewsPreferenceResponse(NewsPreference newsPreference) {
        NewsPreferenceResponse newsPreferenceResponse = modelMapper.map(newsPreference,
                NewsPreferenceResponse.class);

        if (newsPreference.getUser() != null) {
            newsPreferenceResponse.setUsername(newsPreference.getUser().getUsername());
            newsPreferenceResponse.setEmail(newsPreference.getUser().getEmail());
        }

        return newsPreferenceResponse;
    }
}
