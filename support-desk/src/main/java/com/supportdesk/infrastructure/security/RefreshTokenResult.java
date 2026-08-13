package com.supportdesk.infrastructure.security;

import java.util.UUID;

public record RefreshTokenResult(UUID userId, String rawRefreshToken) {}