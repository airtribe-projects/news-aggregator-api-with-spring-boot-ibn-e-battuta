package io.shinmen.airnewsaggregator.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.shinmen.airnewsaggregator.exception.InvalidTokenException;
import io.shinmen.airnewsaggregator.payload.response.JwtTokenRefreshResponse;
import io.shinmen.airnewsaggregator.payload.response.LoginResponse;
import io.shinmen.airnewsaggregator.payload.response.RefreshTokenResponse;
import io.shinmen.airnewsaggregator.payload.response.UserJwtResponse;
import io.shinmen.airnewsaggregator.payload.response.UserResponse;
import io.shinmen.airnewsaggregator.payload.response.VerificationTokenResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final VerificationTokenService verificationTokenService;
    private final EmailService emailService;

    @Transactional
    public void registerUser(String username, String email, String password) {
        UserResponse user = userService.createUser(username, email, password);
        VerificationTokenResponse verificationToken = verificationTokenService
                .createVerificationToken(user.getUserName());
        emailService.sendVerificationEmail(user.getEmail(), verificationToken.getToken());
    }

    @Transactional
    public LoginResponse loginUser(String username, String password) {
        UserJwtResponse user = userService.loginUser(username, password);
        RefreshTokenResponse refreshToken = refreshTokenService.createRefreshToken(user.getUsername());
        return LoginResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .token(user.getJwt())
                .refreshToken(refreshToken.getToken())
                .build();
    }

    @Transactional
    public JwtTokenRefreshResponse refreshToken(String token) {
        validateToken(token);
        return refreshTokenService.refreshToken(token);
    }

    @Transactional
    public void verifyUser(String token) {
        validateToken(token);
        verificationTokenService.verifyUser(token);
    }

    @Transactional
    public void reVerifyUser(String email) {
        VerificationTokenResponse verificationToken = verificationTokenService.reVerifyUser(email);
        emailService.sendVerificationEmail(email, verificationToken.getToken());
    }

    private void validateToken(String token) {
        try {
            UUID.fromString(token);
        } catch (IllegalArgumentException ex) {
            throw new InvalidTokenException("Invalid token: " + token + " provided");
        }
    }
}
