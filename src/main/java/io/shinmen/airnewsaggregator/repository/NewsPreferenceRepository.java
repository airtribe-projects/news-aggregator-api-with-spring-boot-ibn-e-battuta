package io.shinmen.airnewsaggregator.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.shinmen.airnewsaggregator.model.NewsPreference;
import io.shinmen.airnewsaggregator.model.User;

@Repository
public interface NewsPreferenceRepository extends JpaRepository<NewsPreference, Long> {
    Optional<NewsPreference> findByUser(User user);
}
