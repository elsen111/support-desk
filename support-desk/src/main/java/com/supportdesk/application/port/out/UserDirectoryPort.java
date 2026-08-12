package com.supportdesk.application.port.out;

import com.supportdesk.domain.enums.Role;

import java.util.Optional;
import java.util.UUID;

public interface UserDirectoryPort {
    Optional<Role> findRoleById(UUID userId);
}