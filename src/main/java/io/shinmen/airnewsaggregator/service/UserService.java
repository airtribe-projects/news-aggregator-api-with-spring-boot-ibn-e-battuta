package io.shinmen.airnewsaggregator.service;

import java.util.Optional;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.shinmen.airnewsaggregator.exception.RoleNotFoundException;
import io.shinmen.airnewsaggregator.exception.UserAlreadyExistsException;
import io.shinmen.airnewsaggregator.exception.UserNotFoundException;
import io.shinmen.airnewsaggregator.exception.UserUnverifiedException;
import io.shinmen.airnewsaggregator.model.Role;
import io.shinmen.airnewsaggregator.model.User;
import io.shinmen.airnewsaggregator.model.enums.UserRole;
import io.shinmen.airnewsaggregator.payload.response.UserJwtResponse;
import io.shinmen.airnewsaggregator.payload.response.UserResponse;
import io.shinmen.airnewsaggregator.repository.RoleRepository;
import io.shinmen.airnewsaggregator.repository.UserRepository;
import io.shinmen.airnewsaggregator.security.JwtUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @Transactional
    public UserResponse createUser(final String username, final String email, final String password) {
        Optional<User> existingUser = userRepository.findByUsername(username);

        if (existingUser.isPresent()) {
            log.error("User already exists with username: {}", username);

            throw new UserAlreadyExistsException(username);
        }

        existingUser = userRepository.findByEmail(email);

        if (existingUser.isPresent()) {
            log.error("User already exists with email: {}", email);

            throw new UserAlreadyExistsException(email);
        }

        final User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .email(email)
                .build();

        final Role userRole = roleRepository.findByName(UserRole.ROLE_USER)
                .orElseThrow(() -> new RoleNotFoundException(UserRole.ROLE_USER.name()));

        user.getRoles().add(userRole);

        userRepository.save(user);

        log.info("User created successfully with username: {}", username);

        return UserResponse.builder()
                .email(user.getEmail())
                .userName(user.getUsername())
                .build();
    }

    public UserJwtResponse loginUser(final String username, final String password) {

        final User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        if (!user.isEnabled()) {
            log.error("User with username: {} is not verified", username);

            throw new UserUnverifiedException(user.getEmail());
        }

        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        final String jwt = jwtUtils.generateTokenFromUsername(user.getUsername());

        log.info("User with username: {} is logged in", username);

        return UserJwtResponse.builder()
                .email(user.getEmail())
                .username(user.getUsername())
                .jwt(jwt)
                .build();
    }
}
