package io.shinmen.airnewsaggregator.service;

import static io.shinmen.airnewsaggregator.utility.Constants.CACHE_SOURCES;

import java.util.Set;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.shinmen.airnewsaggregator.repository.SourceRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SourceService {

    private final SourceRepository sourceRepository;

    @Transactional(readOnly = true)
    @Cacheable(value = CACHE_SOURCES, key = "'sources'", unless = "#result == null")
    public Set<String> getAllSources() {

        log.info("Getting all sources");

        return sourceRepository.findAllIds();
    }
}
