package io.shinmen.airnewsaggregator.service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.shinmen.airnewsaggregator.exception.RoleNotFoundException;
import io.shinmen.airnewsaggregator.exception.UserAlreadyExistsException;
import io.shinmen.airnewsaggregator.exception.UserAlreadyVerifiedException;
import io.shinmen.airnewsaggregator.exception.UserNotFoundException;
import io.shinmen.airnewsaggregator.exception.UserUnverifiedException;
import io.shinmen.airnewsaggregator.exception.VerificationTokenExpiredException;
import io.shinmen.airnewsaggregator.exception.VerificationTokenNotFoundException;
import io.shinmen.airnewsaggregator.model.RefreshToken;
import io.shinmen.airnewsaggregator.model.Role;
import io.shinmen.airnewsaggregator.model.User;
import io.shinmen.airnewsaggregator.model.VerificationToken;
import io.shinmen.airnewsaggregator.model.enums.UserRole;
import io.shinmen.airnewsaggregator.payload.response.LoginResponse;
import io.shinmen.airnewsaggregator.repository.RoleRepository;
import io.shinmen.airnewsaggregator.repository.UserRepository;
import io.shinmen.airnewsaggregator.repository.VerificationTokenRepository;
import io.shinmen.airnewsaggregator.security.JwtUtils;
import io.shinmen.airnewsaggregator.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final EmailService emailService;

    @Transactional
    public void registerUser(String username, String email, String password) {
        Optional<User> user = userRepository.findByUsername(username);

        if (user.isPresent()) {
            throw new UserAlreadyExistsException("Username is already taken");
        }

        user = userRepository.findByEmail(email);

        if (user.isPresent()) {
            throw new UserAlreadyExistsException("Email is already in use");
        }

        User newUser = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .email(email)
                .build();

        Role userRole = roleRepository.findByName(UserRole.ROLE_USER)
                .orElseThrow(() -> new RoleNotFoundException("Role " + UserRole.ROLE_USER + " was not found"));

        newUser.getRoles().add(userRole);

        userRepository.save(newUser);

        String token = UUID.randomUUID().toString();

        createVerificationToken(newUser, token);

        emailService.sendVerificationEmail(newUser.getEmail(), token);
    }

    @Transactional
    public LoginResponse loginUser(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User with username: " + username + " not found"));

        if (!user.isEnabled()) {
            throw new UserUnverifiedException("Please verify your email before logging in");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwt = jwtUtils.generateJwtToken(userDetails);

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return LoginResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .token(jwt)
                .refreshToken(refreshToken.getToken())
                .build();
    }

    public void createVerificationToken(User user, String token) {
        VerificationToken verificationToken = VerificationToken.builder()
                .user(user)
                .token(token)
                .expiryDate(Instant.now().plusSeconds(24L * 60 * 60))
                .build();

        verificationTokenRepository.save(verificationToken);
    }

    @Transactional
    public void verifyUser(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new VerificationTokenNotFoundException(
                        "Verification token: " + token + " not found or has expired or user is already verified"));

        if (verificationToken.getExpiryDate().isBefore(Instant.now())) {
            verificationTokenRepository.delete(verificationToken);
            throw new VerificationTokenExpiredException("Verification token: " + token + " has expired");
        }

        User user = verificationToken.getUser();
        user.setEnabled(true);
        userRepository.save(user);
        verificationTokenRepository.delete(verificationToken);
    }

    @Transactional
    public void reVerifyUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with email: " + email + " not found"));

        if (!user.isEnabled()) {
            throw new UserAlreadyVerifiedException("User is already verified");
        }

        Optional<VerificationToken> oldToken = verificationTokenRepository.findByUser(user);
        oldToken.ifPresent(verificationTokenRepository::delete);

        String token = UUID.randomUUID().toString();

        createVerificationToken(user, token);

        emailService.sendVerificationEmail(user.getEmail(), token);
    }
}
