package io.shinmen.airnewsaggregator.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.shinmen.airnewsaggregator.model.Article;
import io.shinmen.airnewsaggregator.model.User;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    Optional<Article> findByUserAndUrl(User user, String url);

    Page<Article> findByUserAndIsRead(User user, boolean isRead, Pageable pageable);

    Page<Article> findByUserAndIsFavorite(User user, boolean isFavorite, Pageable pageable);

    Optional<Article> findByUserAndId(User user, Long id);
}
