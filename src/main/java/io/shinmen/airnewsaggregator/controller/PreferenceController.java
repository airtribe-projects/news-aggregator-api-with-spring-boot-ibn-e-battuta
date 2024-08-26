package io.shinmen.airnewsaggregator.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.shinmen.airnewsaggregator.payload.request.PreferenceRequest;
import io.shinmen.airnewsaggregator.payload.response.PreferenceResponse;
import io.shinmen.airnewsaggregator.security.UserDetailsImpl;
import io.shinmen.airnewsaggregator.service.PreferenceService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/preferences")
@RequiredArgsConstructor
@Slf4j
public class PreferenceController {

    private final PreferenceService preferenceService;

    @GetMapping
    public ResponseEntity<PreferenceResponse> getPreferences(@AuthenticationPrincipal UserDetailsImpl userDetails) {

        log.info("Received request to get preferences for user: {}", userDetails.getUsername());

        PreferenceResponse preference = preferenceService.getPreferencesForUser(userDetails.getUsername());
        return ResponseEntity.ok(preference);
    }

    @PutMapping
    public ResponseEntity<PreferenceResponse> updatePreferences(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody PreferenceRequest updatedPreferences) {

        log.info("Received request to update preferences for user: {}", userDetails.getUsername());

        PreferenceResponse preference = preferenceService.updatePreferences(userDetails.getUsername(),
                updatedPreferences);
        return ResponseEntity.ok(preference);
    }
}