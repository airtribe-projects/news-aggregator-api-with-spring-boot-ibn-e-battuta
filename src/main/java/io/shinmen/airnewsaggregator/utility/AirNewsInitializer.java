package io.shinmen.airnewsaggregator.utility;

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import io.shinmen.airnewsaggregator.model.Role;
import io.shinmen.airnewsaggregator.model.User;
import io.shinmen.airnewsaggregator.model.enums.UserRole;
import io.shinmen.airnewsaggregator.repository.RoleRepository;
import io.shinmen.airnewsaggregator.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AirNewsInitializer {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    // These values can be configured in the application.properties or
    // application.yml
    @Value("${air-news-aggregator.admin.username}")
    private String adminUsername;

    @Value("${air-news-aggregator.admin.password}")
    private String adminPassword;

    @Value("${air-news-aggregator.admin.email}")
    private String adminEmail;

    @PostConstruct
    public void initAdminUser() {
        // Ensure roles exist first
        initRoles();

        // Check if an admin user exists
        Optional<User> adminUserOpt = userRepository.findByUsername(adminUsername);
        if (adminUserOpt.isEmpty()) {
            // Create admin user with ROLE_ADMIN
            User adminUser = User.builder()
                    .username(adminUsername)
                    .password(passwordEncoder.encode(adminPassword))
                    .email(adminEmail)
                    .enabled(true)
                    .roles(Set.of(roleRepository.findByName(UserRole.ROLE_ADMIN)
                            .orElseThrow(() -> new RuntimeException("Error: ROLE_ADMIN is not found."))))
                    .build();

            userRepository.save(adminUser);
        }
    }

    private void initRoles() {
        if (!roleRepository.findByName(UserRole.ROLE_USER).isPresent()) {
            Role user = Role.builder().name(UserRole.ROLE_USER).build();
            roleRepository.save(user);
        }

        if (!roleRepository.findByName(UserRole.ROLE_ADMIN).isPresent()) {
            Role admin = Role.builder().name(UserRole.ROLE_ADMIN).build();
            roleRepository.save(admin);
        }
    }
}
