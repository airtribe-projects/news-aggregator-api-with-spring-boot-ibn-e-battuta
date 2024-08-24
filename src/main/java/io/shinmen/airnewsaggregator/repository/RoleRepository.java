package io.shinmen.airnewsaggregator.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.shinmen.airnewsaggregator.model.Role;
import io.shinmen.airnewsaggregator.model.enums.UserRole;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(UserRole name);
}
