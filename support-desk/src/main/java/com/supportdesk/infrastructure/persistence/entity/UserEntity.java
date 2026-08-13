package com.supportdesk.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Column(nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CommentEntity.RoleJpa role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<RefreshTokenEntity> refreshTokens = new ArrayList<>();

    protected UserEntity() {
    }

    public UserEntity(UUID id, String username, String passwordHash, CommentEntity.RoleJpa role) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public CommentEntity.RoleJpa getRole() { return role; }
    public void setRole(CommentEntity.RoleJpa role) { this.role = role; }
    public List<RefreshTokenEntity> getRefreshTokens() { return refreshTokens; }
    public void setRefreshTokens(List<RefreshTokenEntity> refreshTokens) { this.refreshTokens = refreshTokens; }
}