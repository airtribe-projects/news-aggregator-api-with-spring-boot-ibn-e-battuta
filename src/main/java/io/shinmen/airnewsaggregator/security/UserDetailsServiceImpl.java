package io.shinmen.airnewsaggregator.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.shinmen.airnewsaggregator.exception.UserUnverifiedException;
import io.shinmen.airnewsaggregator.model.User;
import io.shinmen.airnewsaggregator.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        final User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        if (!user.isEnabled()) {
            throw new UserUnverifiedException(user.getEmail());
        }

        return UserDetailsImpl.build(user);
    }
}
