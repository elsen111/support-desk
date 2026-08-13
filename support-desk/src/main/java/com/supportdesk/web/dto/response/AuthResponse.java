package com.supportdesk.web.dto.response;

public record AuthResponse(
        String accessToken,
        String tokenType,
        String refreshToken,
        UserResponse user
) {
    public static AuthResponse of(
            String accessToken,
            String refreshToken,
            UserResponse user
    ) {
        return new AuthResponse (accessToken, "Bearer", refreshToken, user);
    }
}