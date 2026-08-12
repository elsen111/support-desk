package com.supportdesk.infrastructure.persistence.adapter;

import com.supportdesk.application.port.out.UserDirectoryPort;
import com.supportdesk.domain.enums.Role;
import com.supportdesk.infrastructure.persistence.repository.UserJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class UserDirectoryAdapter implements UserDirectoryPort {

    private final UserJpaRepository userJpaRepository;

    public UserDirectoryAdapter(UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public Optional<Role> findRoleById(UUID userId) {
        return userJpaRepository.findById(userId)
                .map(u -> Role.valueOf(u.getRole().name()));
    }
}