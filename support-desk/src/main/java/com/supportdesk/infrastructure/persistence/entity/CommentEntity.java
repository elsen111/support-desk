package com.supportdesk.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "comments")
public class CommentEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private TicketEntity ticket;

    @Column(name = "author_id", nullable = false)
    private UUID authorId;

    @Enumerated(EnumType.STRING)
    @Column(name = "author_role", nullable = false, length = 20)
    private RoleJpa authorRole;

    @Column(nullable = false, length = 2000)
    private String content;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected CommentEntity() {
    }

    public CommentEntity(UUID id, TicketEntity ticket, UUID authorId, RoleJpa authorRole, String content, Instant createdAt) {
        this.id = id;
        this.ticket = ticket;
        this.authorId = authorId;
        this.authorRole = authorRole;
        this.content = content;
        this.createdAt = createdAt;
    }

    public enum RoleJpa { CUSTOMER, AGENT, ADMIN }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public TicketEntity getTicket() { return ticket; }
    public void setTicket(TicketEntity ticket) { this.ticket = ticket; }
    public UUID getAuthorId() { return authorId; }
    public void setAuthorId(UUID authorId) { this.authorId = authorId; }
    public RoleJpa getAuthorRole() { return authorRole; }
    public void setAuthorRole(RoleJpa authorRole) { this.authorRole = authorRole; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}