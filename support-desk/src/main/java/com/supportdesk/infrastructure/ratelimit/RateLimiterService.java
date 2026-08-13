package com.supportdesk.infrastructure.ratelimit;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class RateLimiterService {

    private final ConcurrentMap<String, Bucket> buckets = new ConcurrentHashMap<>();
    private final RateLimitProperties properties;

    public RateLimiterService(RateLimitProperties properties) {
        this.properties = properties;
    }

    public boolean tryConsume(String key, boolean authenticated) {
        Bucket bucket = buckets.computeIfAbsent(key, k -> newBucket(authenticated));
        return bucket.tryConsume(1);
    }

    private Bucket newBucket(boolean authenticated) {
        long capacity = authenticated ? properties.getAuthenticatedCapacity() : properties.getAnonymousCapacity();
        long refillPerMinute = authenticated ? properties.getAuthenticatedRefillPerMinute() : properties.getAnonymousRefillPerMinute();

        Bandwidth limit = Bandwidth.classic(capacity,
                Refill.greedy(refillPerMinute, Duration.ofMinutes(1)));
        return Bucket.builder().addLimit(limit).build();
    }
}