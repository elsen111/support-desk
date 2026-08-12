package com.supportdesk.web.dto.response;

import java.time.Instant;
import java.util.UUID;

public record CommentResponse(
        UUID id,
        UUID authorId,
        String authorRole,
        String content,
        Instant createdAt
) {}