package io.shinmen.airnewsaggregator.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.shinmen.airnewsaggregator.model.Preference;
import io.shinmen.airnewsaggregator.model.User;

@Repository
public interface PreferenceRepository extends JpaRepository<Preference, Long> {
    Optional<Preference> findByUser(User user);
}
