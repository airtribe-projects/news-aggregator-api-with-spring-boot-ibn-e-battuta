package io.shinmen.airnewsaggregator.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.shinmen.airnewsaggregator.exception.PreferencesNotFoundException;
import io.shinmen.airnewsaggregator.model.Preference;
import io.shinmen.airnewsaggregator.model.Source;
import io.shinmen.airnewsaggregator.model.User;
import io.shinmen.airnewsaggregator.payload.request.PreferenceRequest;
import io.shinmen.airnewsaggregator.payload.response.PreferenceResponse;
import io.shinmen.airnewsaggregator.payload.response.PreferenceResponse.PreferenceResponseBuilder;
import io.shinmen.airnewsaggregator.payload.response.UserResponse;
import io.shinmen.airnewsaggregator.repository.PreferenceRepository;
import io.shinmen.airnewsaggregator.repository.SourceRepository;
import io.shinmen.airnewsaggregator.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PreferenceService {

    private final PreferenceRepository preferenceRepository;
    private final UserRepository userRepository;
    private final SourceRepository sourceRepository;

    @Transactional(readOnly = true)
    public PreferenceResponse getPreferencesForUser(String username) {

        log.debug("Fetching preferences for user: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Preference preference = preferenceRepository.findByUser(user)
                .orElseThrow(() -> new PreferencesNotFoundException("User preferences not found"));

        return convertToPreferenceResponse(preference);
    }

    @Transactional
    public PreferenceResponse updatePreferences(String username,
            PreferenceRequest preferenceRequest) {

        log.debug("Updating preferences for user: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Preference preference = preferenceRepository.findByUser(user).orElse(new Preference());

        preference.setUser(user);

        Optional.ofNullable(preferenceRequest.getCategories()).ifPresent(preference::setCategories);
        Optional.ofNullable(preferenceRequest.getSources()).ifPresent(sourceIds -> {
            List<Source> sources = sourceRepository.findAllById(sourceIds);
            Set<Source> sourcesSet = new HashSet<>(sources);
            preference.setSources(sourcesSet);
        });
        Optional.ofNullable(preferenceRequest.getCountry()).ifPresent(preference::setCountry);
        Optional.ofNullable(preferenceRequest.getLanguage()).ifPresent(preference::setLanguage);

        preferenceRepository.save(preference);
        return convertToPreferenceResponse(preference);
    }

    private PreferenceResponse convertToPreferenceResponse(Preference preference) {

        PreferenceResponseBuilder preferenceResponseBuilder = PreferenceResponse.builder()
                .categories(preference.getCategories())
                .sources(preference.getSources().stream().map(Source::getId)
                        .collect(Collectors.toSet()))
                .country(preference.getCountry() != null ? preference.getCountry().name()
                        : null)
                .language(preference.getLanguage() != null ? preference.getLanguage().name()
                        : null);

        if (preference.getUser() != null) {
            UserResponse user = UserResponse.builder()
                    .email(preference.getUser().getEmail())
                    .userName(preference.getUser().getUsername())
                    .build();

            preferenceResponseBuilder.user(user);
        }

        return preferenceResponseBuilder.build();
    }
}
