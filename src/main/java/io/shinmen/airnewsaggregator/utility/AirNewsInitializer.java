package io.shinmen.airnewsaggregator.utility;

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import io.shinmen.airnewsaggregator.exception.RoleNotFoundException;
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

    @Value("${air-news-aggregator.admin.username}")
    private String adminUsername;

    @Value("${air-news-aggregator.admin.password}")
    private String adminPassword;

    @Value("${air-news-aggregator.admin.email}")
    private String adminEmail;

    @PostConstruct
    public void initAdminUser() {
        initRoles();

        Optional<User> adminUserOpt = userRepository.findByUsername(adminUsername);
        if (adminUserOpt.isEmpty()) {
            final User adminUser = User.builder()
                    .username(adminUsername)
                    .password(passwordEncoder.encode(adminPassword))
                    .email(adminEmail)
                    .enabled(true)
                    .roles(Set.of(roleRepository.findByName(UserRole.ROLE_ADMIN)
                            .orElseThrow(() -> new RoleNotFoundException(UserRole.ROLE_ADMIN.name()))))
                    .build();

            userRepository.save(adminUser);
        }
    }

    private void initRoles() {
        if (!roleRepository.findByName(UserRole.ROLE_USER).isPresent()) {
            final Role user = Role.builder().name(UserRole.ROLE_USER).build();
            roleRepository.save(user);
        }

        if (!roleRepository.findByName(UserRole.ROLE_ADMIN).isPresent()) {
            final Role admin = Role.builder().name(UserRole.ROLE_ADMIN).build();
            roleRepository.save(admin);
        }
    }
}
