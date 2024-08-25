package io.shinmen.airnewsaggregator.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.shinmen.airnewsaggregator.model.NewsArticle;
import io.shinmen.airnewsaggregator.model.User;

@Repository
public interface NewsArticleRepository extends JpaRepository<NewsArticle, Long> {
    Optional<NewsArticle> findByUserAndUrl(User user, String url);

    Page<NewsArticle> findByUserAndIsRead(User user, boolean isRead, Pageable pageable);

    Page<NewsArticle> findByUserAndIsFavorite(User user, boolean isFavorite, Pageable pageable);

    Optional<NewsArticle> findByUserAndId(User user, Long id);
}
