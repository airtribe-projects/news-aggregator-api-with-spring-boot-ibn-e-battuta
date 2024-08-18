package io.shinmen.airnewsaggregator.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "news_articles")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsArticle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "article_url", nullable = false)
    private String articleUrl;

    @Column(name = "article_title", nullable = false)
    private String articleTitle;

    @Column(name = "article_author")
    private String articleAuthor;

    @Column(name = "article_source")
    private String articleSource;

    @Column(name = "article_published_date")
    private Date articlePublishedDate;

    @Column(name = "is_read")
    private boolean isRead;

    @Column(name = "is_favorite")
    private boolean isFavorite;
}
