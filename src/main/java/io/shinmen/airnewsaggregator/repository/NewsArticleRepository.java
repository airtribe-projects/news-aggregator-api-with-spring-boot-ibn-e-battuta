package io.shinmen.airnewsaggregator.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.shinmen.airnewsaggregator.model.NewsArticle;
import io.shinmen.airnewsaggregator.model.User;

@Repository
public interface NewsArticleRepository extends JpaRepository<NewsArticle, Long> {
    Optional<NewsArticle> findByUserAndUrl(User user, String url);

    List<NewsArticle> findByUserAndIsRead(User user, boolean isRead);

    List<NewsArticle> findByUserAndIsFavorite(User user, boolean isFavorite);

    Optional<NewsArticle> findByUserAndId(User user, Long id);
}
