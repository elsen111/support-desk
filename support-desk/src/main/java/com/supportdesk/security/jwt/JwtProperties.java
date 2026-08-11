package com.supportdesk.security.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
public class JwtProperties {

    private Duration accessTokenExpiration;

    private String secret;
    private long accessExpiration;
    private long refreshExpiration;

    public long getAccessExpiration() {
        return accessTokenExpiration != null ? accessTokenExpiration.toMillis() : 0;
    }

}
