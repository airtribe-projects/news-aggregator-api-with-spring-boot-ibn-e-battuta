package io.shinmen.airnewsaggregator.repository;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import io.shinmen.airnewsaggregator.model.User;
import io.shinmen.airnewsaggregator.model.VerificationToken;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByToken(String token);

    Optional<VerificationToken> findByUser(User user);

    @Modifying
    @Transactional
    void deleteByExpiryDateBefore(Instant now);
}
