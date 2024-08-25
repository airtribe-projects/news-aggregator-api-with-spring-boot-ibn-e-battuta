package io.shinmen.airnewsaggregator.service;

import java.time.Instant;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.shinmen.airnewsaggregator.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VerificationTokenService {

    private final VerificationTokenRepository verificationTokenRepository;
    
    @Scheduled(fixedRate = 24 * 60 * 60 * 1000)
    @Transactional
    public void deleteExpiredTokens() {
        verificationTokenRepository.deleteByExpiryDateBefore(Instant.now());
    }
}
