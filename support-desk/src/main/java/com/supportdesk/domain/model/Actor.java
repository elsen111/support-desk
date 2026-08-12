package com.supportdesk.domain.model;

import com.supportdesk.domain.enums.Role;

import java.util.Objects;
import java.util.UUID;


public final class Actor {

    private final UUID userId;
    private final Role role;

    public Actor(UUID userId, Role role) {
        this.userId = Objects.requireNonNull(userId, "userId must not be null");
        this.role = Objects.requireNonNull(role, "role must not be null");
    }

    public UUID userId() {
        return userId;
    }

    public Role role() {
        return role;
    }

    public boolean isAdmin() {
        return role == Role.ADMIN;
    }

    public boolean isAgent() {
        return role == Role.AGENT;
    }

    public boolean isCustomer() {
        return role == Role.CUSTOMER;
    }
}