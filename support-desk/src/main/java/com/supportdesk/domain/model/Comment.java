package com.supportdesk.domain.model;

import com.supportdesk.domain.enums.Role;
import com.supportdesk.domain.exception.InvalidTicketDataException;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class Comment {

    private final UUID id;
    private final UUID ticketId;
    private final UUID authorId;
    private final Role authorRole;
    private final String content;
    private final Instant createdAt;

    private Comment(UUID id, UUID ticketId, UUID authorId, Role authorRole, String content, Instant createdAt) {
        this.id = id;
        this.ticketId = ticketId;
        this.authorId = authorId;
        this.authorRole = authorRole;
        this.content = content;
        this.createdAt = createdAt;
    }

    public static Comment create(UUID id, UUID ticketId, UUID authorId, Role authorRole, String content, Instant now) {
        if (content == null || content.isBlank()) {
            throw new InvalidTicketDataException("Comment content must not be blank");
        }
        if (content.length() > 2000) {
            throw new InvalidTicketDataException("Comment content must not exceed 2000 characters");
        }
        return new Comment(id, ticketId, authorId, authorRole, content.trim(), now);
    }

    public static Comment reconstitute(UUID id, UUID ticketId, UUID authorId, Role authorRole, String content, Instant createdAt) {
        return new Comment(id, ticketId, authorId, authorRole, content, createdAt);
    }

    public UUID getId() { return id; }
    public UUID getTicketId() { return ticketId; }
    public UUID getAuthorId() { return authorId; }
    public Role getAuthorRole() { return authorRole; }
    public String getContent() { return content; }
    public Instant getCreatedAt() { return createdAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Comment comment)) return false;
        return Objects.equals(id, comment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}