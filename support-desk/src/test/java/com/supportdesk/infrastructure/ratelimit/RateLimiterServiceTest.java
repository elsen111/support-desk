package com.supportdesk.infrastructure.ratelimit;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RateLimiterServiceTest {

    @Test
    void allowsRequestsUpToCapacityThenBlocks() {
        RateLimitProperties properties = new RateLimitProperties();
        properties.setAuthenticatedCapacity(3);
        properties.setAuthenticatedRefillPerMinute(3);
        RateLimiterService service = new RateLimiterService(properties);

        assertThat(service.tryConsume("user:abc", true)).isTrue();
        assertThat(service.tryConsume("user:abc", true)).isTrue();
        assertThat(service.tryConsume("user:abc", true)).isTrue();
        assertThat(service.tryConsume("user:abc", true)).isFalse();
    }

    @Test
    void differentKeysHaveIndependentBuckets() {
        RateLimitProperties properties = new RateLimitProperties();
        properties.setAuthenticatedCapacity(1);
        properties.setAuthenticatedRefillPerMinute(1);
        RateLimiterService service = new RateLimiterService(properties);

        assertThat(service.tryConsume("user:a", true)).isTrue();
        assertThat(service.tryConsume("user:a", true)).isFalse();
        assertThat(service.tryConsume("user:b", true)).isTrue();
    }

    @Test
    void anonymousAndAuthenticatedUseDifferentLimits() {
        RateLimitProperties properties = new RateLimitProperties();
        properties.setAuthenticatedCapacity(5);
        properties.setAuthenticatedRefillPerMinute(5);
        properties.setAnonymousCapacity(1);
        properties.setAnonymousRefillPerMinute(1);
        RateLimiterService service = new RateLimiterService(properties);

        assertThat(service.tryConsume("ip:1.2.3.4", false)).isTrue();
        assertThat(service.tryConsume("ip:1.2.3.4", false)).isFalse();

        assertThat(service.tryConsume("user:x", true)).isTrue();
        assertThat(service.tryConsume("user:x", true)).isTrue();
    }
}