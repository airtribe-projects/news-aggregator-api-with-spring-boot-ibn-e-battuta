package io.shinmen.airnewsaggregator.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.shinmen.airnewsaggregator.exception.RefreshTokenExpiredException;
import io.shinmen.airnewsaggregator.exception.RefreshTokenNotFoundException;
import io.shinmen.airnewsaggregator.exception.UserNotFoundException;
import io.shinmen.airnewsaggregator.model.RefreshToken;
import io.shinmen.airnewsaggregator.model.User;
import io.shinmen.airnewsaggregator.payload.response.JwtTokenRefreshResponse;
import io.shinmen.airnewsaggregator.payload.response.RefreshTokenResponse;
import io.shinmen.airnewsaggregator.repository.RefreshTokenRepository;
import io.shinmen.airnewsaggregator.repository.UserRepository;
import io.shinmen.airnewsaggregator.security.JwtUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    @Value("${air-news-aggregator.app.jwtRefreshExpirationMs}")
    private long refreshTokenDurationMs;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    @Transactional
    public RefreshTokenResponse createRefreshToken(final String username) {
        final User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        final RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
                .build();

        refreshTokenRepository.save(refreshToken);

        log.info("Refresh token created for user: {}", username);

        return RefreshTokenResponse.builder()
                .token(refreshToken.getToken())
                .build();
    }

    @Transactional
    @Scheduled(fixedRate = 24 * 60 * 60 * 1000)
    public void deleteExpiredTokens() {
        log.info("Deleting expired refresh tokens");

        refreshTokenRepository.deleteByExpiryDateBefore(Instant.now());
    }

    @Transactional
    public JwtTokenRefreshResponse refreshToken(final String token) {
        final RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RefreshTokenNotFoundException(token));

        if (refreshToken.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(refreshToken);

            log.error("Refresh token: {} has expired", token);

            throw new RefreshTokenExpiredException(token);
        }

        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));

        refreshTokenRepository.save(refreshToken);

        final String jwt = jwtUtils.generateTokenFromUsername(refreshToken.getUser().getUsername());

        log.info("Refresh token: {} has been refreshed", token);

        return JwtTokenRefreshResponse.builder()
                .accessToken(jwt)
                .refreshToken(refreshToken.getToken())
                .build();
    }
}
