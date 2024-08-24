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
    int deleteByUser(User user);

    @Modifying
    @Transactional
    void deleteByToken(String token);

    @Modifying
    @Transactional
    void deleteByExpiryDateBefore(Instant expiryDate);
}
