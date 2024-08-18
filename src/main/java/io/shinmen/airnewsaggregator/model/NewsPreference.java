package io.shinmen.airnewsaggregator.model;

import io.shinmen.airnewsaggregator.model.enums.Category;
import io.shinmen.airnewsaggregator.model.enums.Country;
import io.shinmen.airnewsaggregator.model.enums.Language;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "news_preferences")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "User is required")
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Size(max = 10, message = "Maximum 10 categories allowed")
    @ElementCollection
    @CollectionTable(name = "news_preference_categories", joinColumns = @JoinColumn(name = "preference_id"))
    @Column(name = "category")
    @Builder.Default
    private Set<Category> categories = new HashSet<>();

    @Size(max = 20, message = "Maximum 20 sources allowed")
    @ElementCollection
    @CollectionTable(name = "news_preference_sources", joinColumns = @JoinColumn(name = "preference_id"))
    @Column(name = "source")
    @Builder.Default
    private Set<String> sources = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "country")
    private Country country;

    @Enumerated(EnumType.STRING)
    @Column(name = "language")
    private Language language;
}
