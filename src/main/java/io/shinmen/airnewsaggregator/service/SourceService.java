package io.shinmen.airnewsaggregator.service;

import java.util.Set;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.shinmen.airnewsaggregator.repository.SourceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SourceService {

    private final SourceRepository sourceRepository;

    @Transactional(readOnly = true)
    @Cacheable(value = "sources", key = "'sources'", unless = "#result == null")
    public Set<String> getAllSources() {
        return sourceRepository.findAllIds();
    }
}
