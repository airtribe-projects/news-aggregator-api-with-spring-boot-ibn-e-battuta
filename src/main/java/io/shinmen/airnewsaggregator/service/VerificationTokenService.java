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
import io.shinmen.airnewsaggregator.service.helper.ServiceHelper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VerificationTokenService {

    @Value("${air-news-aggregator.app.verifyTokenExpirationMs:86400000}")
    private Long verificationTokenDurationMs;

    private final VerificationTokenRepository verificationTokenRepository;
    private final UserRepository userRepository;

    @Scheduled(fixedRate = 24 * 60 * 60 * 1000)
    @Transactional
    public void deleteExpiredTokens() {
        verificationTokenRepository.deleteByExpiryDateBefore(Instant.now());
    }

    @Transactional
    public VerificationTokenResponse createVerificationToken(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(
                        ServiceHelper.getEntityNotFoundMessage("User", "username", username)));

        VerificationToken verificationToken = VerificationToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(verificationTokenDurationMs))
                .build();

        verificationTokenRepository.save(verificationToken);
        return VerificationTokenResponse.builder().token(verificationToken.getToken()).build();
    }

    @Transactional
    public void verifyUser(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new VerificationTokenNotFoundException(
                        ServiceHelper.getEntityNotFoundMessage("Verification token", "token", token)));

        if (verificationToken.getExpiryDate().isBefore(Instant.now())) {
            verificationTokenRepository.delete(verificationToken);
            throw new VerificationTokenExpiredException("Verification token: " + token + " has expired");
        }

        User user = verificationToken.getUser();
        user.setEnabled(true);
        userRepository.save(user);
        verificationTokenRepository.delete(verificationToken);
    }

    @Transactional
    public VerificationTokenResponse reVerifyUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(
                        ServiceHelper.getEntityNotFoundMessage("User", "email", email)));

        if (!user.isEnabled()) {
            throw new UserAlreadyVerifiedException("User is already verified");
        }

        Optional<VerificationToken> oldToken = verificationTokenRepository.findByUser(user);
        oldToken.ifPresent(verificationTokenRepository::delete);

        VerificationToken verificationToken = VerificationToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(verificationTokenDurationMs))
                .build();

        verificationTokenRepository.save(verificationToken);

        return VerificationTokenResponse.builder()
                .token(verificationToken.getToken())
                .build();
    }
}
