package io.shinmen.airnewsaggregator.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.shinmen.airnewsaggregator.model.User;
import io.shinmen.airnewsaggregator.model.NewsArticle;

@Repository
public interface NewsArticleRepository extends JpaRepository<NewsArticle, Long> {
    Optional<NewsArticle> findByUserAndArticleUrl(User user, String articleUrl);

    List<NewsArticle> findByUserAndIsRead(User user, boolean isRead);

    List<NewsArticle> findByUserAndIsFavorite(User user, boolean isFavorite);

    Optional<NewsArticle> findByUserAndId(User user, Long id);
}
