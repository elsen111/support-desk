package com.supportdesk.infrastructure.security;

import com.supportdesk.infrastructure.persistence.entity.CommentEntity;
import com.supportdesk.infrastructure.persistence.entity.UserEntity;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {

    private static final String SECRET = "test-secret-key-for-jwt-signing-minimum-32-characters-long";

    @Test
    void generatesTokenWithExpectedClaims() {
        JwtTokenProvider provider = new JwtTokenProvider(SECRET, 3_600_000);
        CustomUserDetails userDetails = agentDetails("agent1");

        String token = provider.generateToken(userDetails);

        assertThat(provider.validateToken(token)).isTrue();
        assertThat(provider.getUsername(token)).isEqualTo("agent1");
        assertThat(provider.getRole(token)).isEqualTo("AGENT");
        assertThat(provider.getUserId(token)).isEqualTo(userDetails.getUserId());
    }

    @Test
    void rejectsTokenSignedWithDifferentSecret() {
        JwtTokenProvider issuer = new JwtTokenProvider(SECRET, 3_600_000);
        JwtTokenProvider verifier = new JwtTokenProvider("a-totally-different-secret-key-of-32-chars!", 3_600_000);

        String token = issuer.generateToken(agentDetails("agent1"));

        assertThat(verifier.validateToken(token)).isFalse();
    }

    @Test
    void rejectsExpiredToken() throws InterruptedException {
        JwtTokenProvider provider = new JwtTokenProvider(SECRET, 1);
        String token = provider.generateToken(agentDetails("agent1"));

        Thread.sleep(15);

        assertThat(provider.validateToken(token)).isFalse();
    }

    @Test
    void rejectsMalformedToken() {
        JwtTokenProvider provider = new JwtTokenProvider(SECRET, 3_600_000);

        assertThat(provider.validateToken("this.is-not.valid")).isFalse();
    }

    private CustomUserDetails agentDetails(String username) {
        UserEntity entity = new UserEntity(UUID.randomUUID(), username, "hashed-pw", CommentEntity.RoleJpa.AGENT);
        return new CustomUserDetails(entity);
    }
}