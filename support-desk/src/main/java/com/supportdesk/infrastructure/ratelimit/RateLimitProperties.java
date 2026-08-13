package com.supportdesk.infrastructure.ratelimit;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "supportdesk.rate-limit")
public class RateLimitProperties {

    private boolean enabled = true;
    private long authenticatedCapacity = 100;
    private long authenticatedRefillPerMinute = 100;
    private long anonymousCapacity = 20;
    private long anonymousRefillPerMinute = 20;

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public long getAuthenticatedCapacity() { return authenticatedCapacity; }
    public void setAuthenticatedCapacity(long authenticatedCapacity) { this.authenticatedCapacity = authenticatedCapacity; }
    public long getAuthenticatedRefillPerMinute() { return authenticatedRefillPerMinute; }
    public void setAuthenticatedRefillPerMinute(long authenticatedRefillPerMinute) { this.authenticatedRefillPerMinute = authenticatedRefillPerMinute; }
    public long getAnonymousCapacity() { return anonymousCapacity; }
    public void setAnonymousCapacity(long anonymousCapacity) { this.anonymousCapacity = anonymousCapacity; }
    public long getAnonymousRefillPerMinute() { return anonymousRefillPerMinute; }
    public void setAnonymousRefillPerMinute(long anonymousRefillPerMinute) { this.anonymousRefillPerMinute = anonymousRefillPerMinute; }
}