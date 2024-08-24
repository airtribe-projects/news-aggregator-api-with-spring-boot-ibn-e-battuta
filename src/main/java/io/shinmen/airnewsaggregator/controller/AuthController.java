package io.shinmen.airnewsaggregator.controller;

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
import io.shinmen.airnewsaggregator.payload.response.LoginResponse;
import io.shinmen.airnewsaggregator.payload.response.MessageResponse;
import io.shinmen.airnewsaggregator.payload.response.TokenRefreshResponse;
import io.shinmen.airnewsaggregator.service.AuthService;
import io.shinmen.airnewsaggregator.service.RefreshTokenService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "*", maxAge = 3600)
@Validated
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/register")
    public ResponseEntity<MessageResponse> register(@Valid @RequestBody SignupRequest signUpRequest) {
        authService.registerUser(signUpRequest.getUsername(), signUpRequest.getEmail(), signUpRequest.getPassword());
        return ResponseEntity.ok(new MessageResponse("User registered successfully and verification email is sent"));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = authService.loginUser(loginRequest.getUsername(), loginRequest.getPassword());
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<TokenRefreshResponse> refresh(@Valid @RequestBody TokenRefreshRequest tokenRefreshRequest) {
        TokenRefreshResponse tokenRefreshResponse = refreshTokenService
                .refreshToken(tokenRefreshRequest.getRefreshToken());
        return ResponseEntity.ok(tokenRefreshResponse);
    }

    @GetMapping("/verify")
    public ResponseEntity<MessageResponse> confirmUserAccount(@RequestParam String token) {
        authService.verifyUser(token);
        return ResponseEntity.ok(new MessageResponse("User successfully verified"));
    }

    @PostMapping("/re-verify")
    public ResponseEntity<MessageResponse> resendVerificationToken(
            @RequestParam @Email(message = "Email should be valid") String email) {
        authService.reVerifyUser(email);
        return ResponseEntity.ok(new MessageResponse("Verification email successfully sent"));
    }
}
