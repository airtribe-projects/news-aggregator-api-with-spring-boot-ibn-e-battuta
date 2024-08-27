package io.shinmen.airnewsaggregator.service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.shinmen.airnewsaggregator.exception.UserAlreadyVerifiedException;
import io.shinmen.airnewsaggregator.exception.UserNotFoundException;
import io.shinmen.airnewsaggregator.exception.VerificationTokenExpiredException;
import io.shinmen.airnewsaggregator.exception.VerificationTokenNotFoundException;
import io.shinmen.airnewsaggregator.model.User;
import io.shinmen.airnewsaggregator.model.VerificationToken;
import io.shinmen.airnewsaggregator.payload.response.VerificationTokenResponse;
import io.shinmen.airnewsaggregator.repository.UserRepository;
import io.shinmen.airnewsaggregator.repository.VerificationTokenRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerificationTokenService {

    @Value("${air-news-aggregator.app.verifyTokenExpirationMs:86400000}")
    private long verificationTokenDurationMs;

    private final VerificationTokenRepository verificationTokenRepository;
    private final UserRepository userRepository;

    @Scheduled(fixedRate = 24 * 60 * 60 * 1000)
    @Transactional
    public void deleteExpiredTokens() {
        log.info("Deleting expired verification tokens");

        verificationTokenRepository.deleteByExpiryDateBefore(Instant.now());
    }

    @Transactional
    public VerificationTokenResponse createVerificationToken(final String username) {

        log.info("Creating verification token for user: {}", username);

        final User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        final VerificationToken verificationToken = VerificationToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(verificationTokenDurationMs))
                .build();

        verificationTokenRepository.save(verificationToken);

        log.info("Verification token: {} created for user: {}", verificationToken.getToken(), user.getUsername());

        return VerificationTokenResponse.builder()
                .token(verificationToken.getToken())
                .build();
    }

    @Transactional
    public void verifyUser(final String token) {

        log.info("Verifying user with token: {}", token);

        final VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new VerificationTokenNotFoundException(token));

        if (verificationToken.getExpiryDate().isBefore(Instant.now())) {
            verificationTokenRepository.delete(verificationToken);

            log.error("Verification token: {} has expired", token);

            throw new VerificationTokenExpiredException(token);
        }

        final User user = verificationToken.getUser();

        user.setEnabled(true);

        userRepository.save(user);

        verificationTokenRepository.delete(verificationToken);

        log.info("User: {} successfully verified with token: {}", user.getUsername(), token);
    }

    @Transactional
    public VerificationTokenResponse reVerifyUser(final String email) {

        log.info("Re-verifying user with email: {}", email);

        final User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        if (!user.isEnabled()) {
            log.error("User: {} is already verified", user.getEmail());

            throw new UserAlreadyVerifiedException(user.getEmail());
        }

        final Optional<VerificationToken> oldToken = verificationTokenRepository.findByUser(user);
        oldToken.ifPresent(verificationTokenRepository::delete);

        final VerificationToken verificationToken = VerificationToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(verificationTokenDurationMs))
                .build();

        verificationTokenRepository.save(verificationToken);

        log.info("Re-verification token: {} created for user: {}", verificationToken.getToken(), user.getUsername());

        return VerificationTokenResponse.builder()
                .token(verificationToken.getToken())
                .build();
    }
}
