package com.supportdesk.web.dto.response;

import com.supportdesk.infrastructure.persistence.entity.UserEntity;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String username,
        String role
) {

    public static UserResponse from(UserEntity user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getRole().name());
    }

}
