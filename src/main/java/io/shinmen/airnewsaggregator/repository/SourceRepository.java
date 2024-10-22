package io.shinmen.airnewsaggregator.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import io.shinmen.airnewsaggregator.model.Source;

public interface SourceRepository extends JpaRepository<Source, String> {
    @Query("SELECT s.id FROM Source s")
    Set<String> findAllIds();
}
