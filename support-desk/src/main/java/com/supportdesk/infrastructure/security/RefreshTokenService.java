package com.supportdesk.infrastructure.security;

import com.supportdesk.infrastructure.persistence.entity.RefreshTokenEntity;
import com.supportdesk.infrastructure.persistence.entity.UserEntity;
import com.supportdesk.infrastructure.persistence.repository.RefreshTokenJpaRepository;
import com.supportdesk.infrastructure.persistence.repository.UserJpaRepository;
import com.supportdesk.web.exception.InvalidRefreshTokenException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Clock;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Component
public class RefreshTokenService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int TOKEN_BYTE_LENGTH = 64;

    private final RefreshTokenJpaRepository refreshTokenRepository;
    private final UserJpaRepository userJpaRepository;
    private final Clock clock;
    private final long expirationMillis;

    public RefreshTokenService(RefreshTokenJpaRepository refreshTokenRepository,
                               UserJpaRepository userJpaRepository,
                               Clock clock,
                               @Value("${supportdesk.jwt.refresh-expiration-ms:604800000}") long expirationMillis) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userJpaRepository = userJpaRepository;
        this.clock = clock;
        this.expirationMillis = expirationMillis;
    }

    public String issueToken(UUID userId) {
        UserEntity userRef = userJpaRepository.getReferenceById(userId);

        String rawToken = generateRawToken();
        RefreshTokenEntity entity = new RefreshTokenEntity(
                UUID.randomUUID(),
                userRef,
                hash(rawToken),
                clock.instant().plusMillis(expirationMillis),
                false,
                clock.instant()
        );
        refreshTokenRepository.save(entity);
        return rawToken;
    }

    public RefreshTokenResult validateAndRotate(String rawToken) {
        RefreshTokenEntity entity = refreshTokenRepository.findByTokenHash(hash(rawToken))
                .orElseThrow(() -> new InvalidRefreshTokenException("Refresh token is invalid"));

        if (entity.isRevoked()) {
            throw new InvalidRefreshTokenException("Refresh token has already been used or revoked");
        }
        if (entity.getExpiresAt().isBefore(clock.instant())) {
            throw new InvalidRefreshTokenException("Refresh token has expired");
        }

        entity.setRevoked(true);
        refreshTokenRepository.save(entity);

        UUID userId = entity.getUser().getId();
        String newRawToken = issueToken(userId);
        return new RefreshTokenResult(userId, newRawToken);
    }

    /** Idempotent: revoking an unknown or already-revoked token is a no-op — logout should never fail. */
    public void revoke(String rawToken) {
        refreshTokenRepository.findByTokenHash(hash(rawToken)).ifPresent(entity -> {
            entity.setRevoked(true);
            refreshTokenRepository.save(entity);
        });
    }

    /**
     * Revokes every refresh token belonging to a user — e.g. "log out of all
     * devices". Enabled by the user association; not wired to an endpoint
     * yet, so it's here to use if/when you add one (e.g. DELETE /api/auth/sessions).
     */
    public void revokeAllForUser(UUID userId) {
        List<RefreshTokenEntity> tokens = refreshTokenRepository.findByUser_Id(userId);
        tokens.forEach(t -> t.setRevoked(true));
        refreshTokenRepository.saveAll(tokens);
    }

    private String generateRawToken() {
        byte[] bytes = new byte[TOKEN_BYTE_LENGTH];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hash(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hashed);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }
}