package com.supportdesk.domain.model;

import com.supportdesk.domain.enums.TicketPriority;
import com.supportdesk.domain.enums.TicketStatus;
import com.supportdesk.domain.exception.InvalidStatusTransitionException;
import com.supportdesk.domain.exception.InvalidTicketDataException;
import com.supportdesk.domain.exception.TicketAccessDeniedException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class Ticket {

    private final UUID id;
    private String title;
    private String description;
    private TicketPriority priority;
    private TicketStatus status;
    private final UUID requesterId;
    private UUID assignedAgentId; // nullable
    private final List<Comment> comments = new ArrayList<>();
    private final Instant createdAt;
    private Instant updatedAt;

    private Ticket(UUID id, String title, String description, TicketPriority priority, TicketStatus status,
                   UUID requesterId, UUID assignedAgentId, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = status;
        this.requesterId = requesterId;
        this.assignedAgentId = assignedAgentId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Ticket create(UUID id, String title, String description, TicketPriority priority,
                                UUID requesterId, Instant now) {
        validateTitle(title);
        validateDescription(description);
        Objects.requireNonNull(priority, "priority must not be null");
        Objects.requireNonNull(requesterId, "requesterId must not be null");

        return new Ticket(id, title.trim(), description.trim(), priority, TicketStatus.OPEN,
                requesterId, null, now, now);
    }

    public static Ticket reconstitute(UUID id, String title, String description, TicketPriority priority,
                                      TicketStatus status, UUID requesterId, UUID assignedAgentId,
                                      List<Comment> comments, Instant createdAt, Instant updatedAt) {
        Ticket ticket = new Ticket(id, title, description, priority, status, requesterId, assignedAgentId,
                createdAt, updatedAt);
        ticket.comments.addAll(comments);
        return ticket;
    }

    private static void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new InvalidTicketDataException("Ticket title must not be blank");
        }
        if (title.length() > 200) {
            throw new InvalidTicketDataException("Ticket title must not exceed 200 characters");
        }
    }

    private static void validateDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new InvalidTicketDataException("Ticket description must not be blank");
        }
        if (description.length() > 5000) {
            throw new InvalidTicketDataException("Ticket description must not exceed 5000 characters");
        }
    }


    public void assignTo(UUID agentId, Instant now) {
        Objects.requireNonNull(agentId, "agentId must not be null");
        if (status.isTerminal()) {
            throw new InvalidStatusTransitionException(status, status);
        }
        this.assignedAgentId = agentId;
        if (status == TicketStatus.OPEN) {
            this.status = TicketStatus.IN_PROGRESS;
        }
        this.updatedAt = now;
    }

    public void changeStatus(TicketStatus newStatus, Instant now) {
        Objects.requireNonNull(newStatus, "newStatus must not be null");
        if (!status.canTransitionTo(newStatus)) {
            throw new InvalidStatusTransitionException(status, newStatus);
        }
        this.status = newStatus;
        this.updatedAt = now;
    }

    public void addComment(Comment comment) {
        if (status.isTerminal()) {
            throw new InvalidStatusTransitionException(status, status);
        }
        comments.add(comment);
        this.updatedAt = comment.getCreatedAt();
    }

    public boolean isOwnedBy(UUID userId) {
        return requesterId.equals(userId);
    }

    public boolean isAssignedTo(UUID agentId) {
        return agentId != null && agentId.equals(assignedAgentId);
    }

    public void assertViewableBy(Actor actor) {
        if (actor.isAdmin()) return;
        if (actor.isCustomer() && isOwnedBy(actor.userId())) return;
        if (actor.isAgent() && isAssignedTo(actor.userId())) return;
        throw new TicketAccessDeniedException("Actor " + actor.userId() + " may not view ticket " + id);
    }

    public void assertCommentableBy(Actor actor) {
        assertViewableBy(actor);
    }

    public UUID getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public TicketPriority getPriority() { return priority; }
    public TicketStatus getStatus() { return status; }
    public UUID getRequesterId() { return requesterId; }
    public UUID getAssignedAgentId() { return assignedAgentId; }
    public List<Comment> getComments() { return Collections.unmodifiableList(comments); }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ticket ticket)) return false;
        return Objects.equals(id, ticket.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}