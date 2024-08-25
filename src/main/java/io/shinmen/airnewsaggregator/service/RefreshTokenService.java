package io.shinmen.airnewsaggregator.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.shinmen.airnewsaggregator.exception.RefreshTokenExpiredException;
import io.shinmen.airnewsaggregator.exception.RefreshTokenNotFoundException;
import io.shinmen.airnewsaggregator.model.RefreshToken;
import io.shinmen.airnewsaggregator.model.User;
import io.shinmen.airnewsaggregator.payload.response.TokenRefreshResponse;
import io.shinmen.airnewsaggregator.repository.RefreshTokenRepository;
import io.shinmen.airnewsaggregator.security.JwtUtils;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    @Value("${air-news-aggregator.app.jwtRefreshExpirationMs}")
    private Long refreshTokenDurationMs;

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtils jwtUtils;

    @Transactional
    public RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
                .build();

        refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    @Transactional
    @Scheduled(fixedRate = 24 * 60 * 60 * 1000)
    public void deleteExpiredTokens() {
        refreshTokenRepository.deleteByExpiryDateBefore(Instant.now());
    }

    @Transactional
    public TokenRefreshResponse refreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RefreshTokenNotFoundException("Refresh token: " + token + " was not found"));

        if (refreshToken.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(refreshToken);
            throw new RefreshTokenExpiredException(
                    "Refresh token: " + token + " has expired. Please make a new login request");
        }

        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshTokenRepository.save(refreshToken);

        String jwt = jwtUtils.generateTokenFromUsername(refreshToken.getUser().getUsername());

        return TokenRefreshResponse.builder()
                .accessToken(jwt)
                .refreshToken(refreshToken.getToken())
                .build();
    }
}
