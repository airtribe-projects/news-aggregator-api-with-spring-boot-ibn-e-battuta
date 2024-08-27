package io.shinmen.airnewsaggregator.controller;

import static io.shinmen.airnewsaggregator.utility.Constants.API_AUTH;
import static io.shinmen.airnewsaggregator.utility.Constants.API_AUTH_LOGIN;
import static io.shinmen.airnewsaggregator.utility.Constants.API_AUTH_REFRESH_TOKEN;
import static io.shinmen.airnewsaggregator.utility.Constants.API_AUTH_REGISTER;
import static io.shinmen.airnewsaggregator.utility.Constants.API_AUTH_RE_VERIFY;
import static io.shinmen.airnewsaggregator.utility.Constants.API_AUTH_VERIFY;
import static io.shinmen.airnewsaggregator.utility.Messages.USER_REGISTRATION_MESSAGE;
import static io.shinmen.airnewsaggregator.utility.Messages.USER_RE_VERIFICATION_EMAIL_MESSAGE;
import static io.shinmen.airnewsaggregator.utility.Messages.USER_VERIFICATION_MESSAGE;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.shinmen.airnewsaggregator.payload.request.LoginRequest;
import io.shinmen.airnewsaggregator.payload.request.SignupRequest;
import io.shinmen.airnewsaggregator.payload.request.TokenRefreshRequest;
import io.shinmen.airnewsaggregator.payload.response.JwtTokenRefreshResponse;
import io.shinmen.airnewsaggregator.payload.response.LoginResponse;
import io.shinmen.airnewsaggregator.payload.response.MessageResponse;
import io.shinmen.airnewsaggregator.service.AuthService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@RestController
@RequestMapping(API_AUTH)
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    private final AuthService authService;

    @PostMapping(API_AUTH_REGISTER)
    public ResponseEntity<MessageResponse> register(@Valid @RequestBody final SignupRequest request) {

        log.info("Received signup request for user: {}", request.getUsername());

        authService.registerUser(request.getUsername(), request.getEmail(), request.getPassword());

        return ResponseEntity.ok(MessageResponse.builder()
                .message(USER_REGISTRATION_MESSAGE)
                .build());
    }

    @PostMapping(API_AUTH_LOGIN)
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody final LoginRequest request) {

        log.info("Received login request for user: {}", request.getUsername());

        final LoginResponse loginResponse = authService.loginUser(request.getUsername(), request.getPassword());

        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping(API_AUTH_REFRESH_TOKEN)
    public ResponseEntity<JwtTokenRefreshResponse> refresh(@Valid @RequestBody final TokenRefreshRequest request) {

        log.info("Received refresh token request: {}", request.getToken());

        final JwtTokenRefreshResponse response = authService.refreshToken(request.getToken());

        return ResponseEntity.ok(response);
    }

    @GetMapping(API_AUTH_VERIFY)
    public ResponseEntity<MessageResponse> confirmUserAccount(@RequestParam final String token) {

        log.info("Received verification request for token: {}", token);

        authService.verifyUser(token);

        return ResponseEntity.ok(MessageResponse.builder()
                .message(USER_VERIFICATION_MESSAGE)
                .build());
    }

    @PostMapping(API_AUTH_RE_VERIFY)
    public ResponseEntity<MessageResponse> resendVerificationToken(
            @RequestParam @Email(message = "Email should be valid") final String email) {

        log.info("Received re-verification request for email: {}", email);

        authService.reVerifyUser(email);

        return ResponseEntity.ok(MessageResponse.builder()
                .message(USER_RE_VERIFICATION_EMAIL_MESSAGE)
                .build());
    }
}
