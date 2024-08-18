package io.shinmen.airnewsaggregator.service;

import io.shinmen.airnewsaggregator.model.User;
import io.shinmen.airnewsaggregator.model.NewsPreference;
import io.shinmen.airnewsaggregator.model.enums.Category;
import io.shinmen.airnewsaggregator.model.enums.Country;
import io.shinmen.airnewsaggregator.model.enums.Language;
import io.shinmen.airnewsaggregator.payload.response.NewsPreferenceResponse;
import io.shinmen.airnewsaggregator.repository.NewsPreferenceRepository;
import io.shinmen.airnewsaggregator.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NewsPreferenceServiceTest {

    @Mock
    private NewsPreferenceRepository newsPreferenceRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private NewsPreferenceService newsPreferenceService;

    private User user;
    private NewsPreference newsPreference;
    private NewsPreferenceResponse newsPreferenceResponse;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("test");
        user.setEmail("test@test.com");

        newsPreference = new NewsPreference();
        newsPreference.setUser(user);
        newsPreference.setCountry(Country.US);
        newsPreference.setLanguage(Language.EN);
        Set<Category> categories = Set.of(Category.GENERAL, Category.SCIENCE);
        newsPreference.setCategories(categories);

        newsPreferenceResponse = new NewsPreferenceResponse();
        newsPreferenceResponse.setUsername("test");
        newsPreferenceResponse.setEmail("test@test.com");
        newsPreferenceResponse.setCountry("us");
        newsPreferenceResponse.setLanguage("en");
        newsPreferenceResponse.setCategories(categories);
    }

    @Test
    void getPreferencesForUser_Success() {
        when(userRepository.findByUsername("test")).thenReturn(Optional.of(user));
        when(newsPreferenceRepository.findByUser(user)).thenReturn(Optional.of(newsPreference));
        when(modelMapper.map(newsPreference, NewsPreferenceResponse.class)).thenReturn(newsPreferenceResponse);

        NewsPreferenceResponse result = newsPreferenceService.getPreferencesForUser("test");

        assertNotNull(result);
        assertEquals("test", result.getUsername());
        assertEquals("us", result.getCountry());
        assertEquals("en", result.getLanguage());
        assertTrue(result.getCategories().contains(Category.GENERAL));
    }

    @Test
    void updatePreferences() {
    }
}