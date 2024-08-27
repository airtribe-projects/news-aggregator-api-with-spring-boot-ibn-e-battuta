package io.shinmen.airnewsaggregator.repository;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import io.shinmen.airnewsaggregator.model.RefreshToken;
import io.shinmen.airnewsaggregator.model.User;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    @Modifying
    @Transactional
    int deleteByUser(final User user);

    @Modifying
    @Transactional
    void deleteByToken(final String token);

    @Modifying
    @Transactional
    void deleteByExpiryDateBefore(final Instant expiryDate);
}
