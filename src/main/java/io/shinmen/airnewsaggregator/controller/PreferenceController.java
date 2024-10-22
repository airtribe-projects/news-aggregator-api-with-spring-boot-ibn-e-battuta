package io.shinmen.airnewsaggregator.controller;

import static io.shinmen.airnewsaggregator.utility.Constants.API_PREFERENCES;

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

@Slf4j
@RestController
@RequestMapping(API_PREFERENCES)
@RequiredArgsConstructor
public class PreferenceController {

    private final PreferenceService preferenceService;

    @GetMapping
    public ResponseEntity<PreferenceResponse> getPreferences(
            @AuthenticationPrincipal final UserDetailsImpl userDetails) {

        log.info("Received request to get preferences for user: {}", userDetails.getUsername());

        PreferenceResponse response = preferenceService.getPreferencesForUser(userDetails.getUsername());

        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<PreferenceResponse> updatePreferences(
            @AuthenticationPrincipal final UserDetailsImpl userDetails,
            @Valid @RequestBody final PreferenceRequest request) {

        log.info("Received request to update preferences for user: {}", userDetails.getUsername());

        PreferenceResponse response = preferenceService.updatePreferences(userDetails.getUsername(),
                request);

        return ResponseEntity.ok(response);
    }
}