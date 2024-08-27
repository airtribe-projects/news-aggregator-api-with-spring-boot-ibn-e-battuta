package io.shinmen.airnewsaggregator.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.shinmen.airnewsaggregator.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(final String username);

    Optional<User> findByEmail(final String email);
}
