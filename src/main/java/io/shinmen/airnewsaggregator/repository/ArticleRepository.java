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
    Optional<Article> findByUserAndUrl(final User user, final String url);

    Page<Article> findByUserAndIsRead(final User user, final boolean isRead, final Pageable pageable);

    Page<Article> findByUserAndIsFavorite(final User user, final boolean isFavorite, final Pageable pageable);

    Optional<Article> findByUserAndId(final User user, final Long id);
}
