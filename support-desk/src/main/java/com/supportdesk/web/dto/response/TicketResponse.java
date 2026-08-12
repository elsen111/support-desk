package com.supportdesk.web.dto.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record TicketResponse(
        UUID id,
        String title,
        String description,
        String priority,
        String status,
        UUID requesterId,
        UUID assignedAgentId,
        List<CommentResponse> comments,
        Instant createdAt,
        Instant updatedAt
) {}