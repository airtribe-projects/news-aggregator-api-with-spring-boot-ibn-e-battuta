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
import io.shinmen.airnewsaggregator.service.helper.ServiceHelper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @Transactional
    public UserResponse createUser(String username, String email, String password) {
        Optional<User> existingUser = userRepository.findByUsername(username);

        if (existingUser.isPresent()) {
            throw new UserAlreadyExistsException("Username is already in use");
        }

        existingUser = userRepository.findByEmail(email);

        if (existingUser.isPresent()) {
            throw new UserAlreadyExistsException("Email is already in use");
        }

        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .email(email)
                .build();

        Role userRole = roleRepository.findByName(UserRole.ROLE_USER)
                .orElseThrow(() -> new RoleNotFoundException(
                        ServiceHelper.getEntityNotFoundMessage("Role", "role", UserRole.ROLE_USER.toString())));

        user.getRoles().add(userRole);

        userRepository.save(user);

        return UserResponse.builder().email(user.getEmail()).userName(user.getUsername()).build();
    }

    public UserJwtResponse loginUser(String username, String password) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(
                        ServiceHelper.getEntityNotFoundMessage("User", "username", username)));

        if (!user.isEnabled()) {
            throw new UserUnverifiedException("Please verify email before login");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtUtils.generateTokenFromUsername(user.getUsername());

        return UserJwtResponse.builder()
                .email(user.getEmail())
                .username(user.getUsername())
                .jwt(jwt)
                .build();
    }
}
