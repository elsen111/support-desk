package com.supportdesk.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tickets")
public class TicketEntity {

    @Id
    private UUID id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 5000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PriorityJpa priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusJpa status;

    @Column(name = "requester_id", nullable = false)
    private UUID requesterId;

    @Column(name = "assigned_agent_id")
    private UUID assignedAgentId;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("createdAt ASC")
    private List<com.supportdesk.infrastructure.persistence.entity.CommentEntity> comments = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    private Long version;

    protected TicketEntity() {

    }

    public TicketEntity(UUID id, String title, String description, PriorityJpa priority, StatusJpa status,
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

    public enum PriorityJpa { LOW, MEDIUM, HIGH, URGENT }
    public enum StatusJpa { OPEN, IN_PROGRESS, WAITING_CUSTOMER, RESOLVED, CLOSED }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public PriorityJpa getPriority() { return priority; }
    public void setPriority(PriorityJpa priority) { this.priority = priority; }
    public StatusJpa getStatus() { return status; }
    public void setStatus(StatusJpa status) { this.status = status; }
    public UUID getRequesterId() { return requesterId; }
    public void setRequesterId(UUID requesterId) { this.requesterId = requesterId; }
    public UUID getAssignedAgentId() { return assignedAgentId; }
    public void setAssignedAgentId(UUID assignedAgentId) { this.assignedAgentId = assignedAgentId; }
    public List<com.supportdesk.infrastructure.persistence.entity.CommentEntity> getComments() { return comments; }
    public void setComments(List<com.supportdesk.infrastructure.persistence.entity.CommentEntity> comments) { this.comments = comments; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}