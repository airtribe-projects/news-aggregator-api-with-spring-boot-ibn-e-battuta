package io.shinmen.airnewsaggregator.model;

import java.time.ZonedDateTime;

import org.hibernate.validator.constraints.URL;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "news_articles")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsArticle {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @URL
    @Column(nullable = false, length = 1024)
    private String url;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private String source;

    @Column(nullable = false)
    private ZonedDateTime publishedAt;

    @Column(columnDefinition = "BOOLEAN")
    private boolean isRead;

    @Column(columnDefinition = "BOOLEAN")
    private boolean isFavorite;
}
