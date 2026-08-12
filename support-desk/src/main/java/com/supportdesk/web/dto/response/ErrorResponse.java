package com.supportdesk.web.dto.response;

import java.time.Instant;
import java.util.List;

public record ErrorResponse(
        boolean success,
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        List<String> details
) {
    public static ErrorResponse of(int status, String error, String message, String path) {
        return new ErrorResponse(false, Instant.now(), status, error, message, path, null);
    }

    public static ErrorResponse of(int status, String error, String message, String path, List<String> details) {
        return new ErrorResponse(false, Instant.now(), status, error, message, path, details);
    }
}