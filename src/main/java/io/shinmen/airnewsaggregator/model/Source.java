package io.shinmen.airnewsaggregator.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@Table(name = "sources")
@AllArgsConstructor
@NoArgsConstructor
public class Source {
    @Id
    private String id;

    @Column(nullable = false, length = 512)
    private String name;

    @Column(nullable = false, length = 2048)
    private String description;

    @Column(nullable = false, length = 1024)
    private String url;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String language;

    @Column(nullable = false)
    private String country;
}
