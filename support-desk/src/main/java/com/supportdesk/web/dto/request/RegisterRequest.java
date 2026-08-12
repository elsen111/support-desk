package com.supportdesk.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RegisterRequest(
        @NotBlank String username,
        @NotBlank String password,
        @Pattern(regexp = "CUSTOMER|AGENT|ADMIN") String role
) {}