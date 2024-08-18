package io.shinmen.airnewsaggregator.controller;

import io.shinmen.airnewsaggregator.payload.response.NewsPreferenceResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.shinmen.airnewsaggregator.payload.request.NewsPreferenceUpdateRequest;
import io.shinmen.airnewsaggregator.security.UserDetailsImpl;
import io.shinmen.airnewsaggregator.service.NewsPreferenceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/preferences")
@RequiredArgsConstructor
@Slf4j
public class NewsPreferenceController {

    private final NewsPreferenceService newsPreferenceService;

    @GetMapping
    public ResponseEntity<NewsPreferenceResponse> getPreferences(@AuthenticationPrincipal UserDetailsImpl userDetails) {

        log.info("Received request to get preferences for user: {}", userDetails.getUsername());

        NewsPreferenceResponse response = newsPreferenceService.getPreferencesForUser(userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<NewsPreferenceResponse> updatePreferences(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody NewsPreferenceUpdateRequest updatedPreferences) {

        log.info("Received request to update preferences for user: {}", userDetails.getUsername());

        NewsPreferenceResponse response = newsPreferenceService.updatePreferences(userDetails.getUsername(),
                updatedPreferences);
        return ResponseEntity.ok(response);
    }
}