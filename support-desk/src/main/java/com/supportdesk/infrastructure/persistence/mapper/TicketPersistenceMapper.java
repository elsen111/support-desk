package com.supportdesk.infrastructure.persistence.mapper;

import com.supportdesk.domain.enums.Role;
import com.supportdesk.domain.enums.TicketPriority;
import com.supportdesk.domain.enums.TicketStatus;
import com.supportdesk.domain.model.*;
import com.supportdesk.infrastructure.persistence.entity.CommentEntity;
import com.supportdesk.infrastructure.persistence.entity.TicketEntity;

import java.util.List;
import java.util.stream.Collectors;

public final class TicketPersistenceMapper {

    private TicketPersistenceMapper() {}

    public static Ticket toDomain(TicketEntity entity) {
        List<Comment> comments = entity.getComments().stream()
                .map(TicketPersistenceMapper::commentToDomain)
                .collect(Collectors.toList());

        return Ticket.reconstitute(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                TicketPriority.valueOf(entity.getPriority().name()),
                TicketStatus.valueOf(entity.getStatus().name()),
                entity.getRequesterId(),
                entity.getAssignedAgentId(),
                comments,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private static Comment commentToDomain(CommentEntity entity) {
        return Comment.reconstitute(
                entity.getId(),
                entity.getTicket().getId(),
                entity.getAuthorId(),
                Role.valueOf(entity.getAuthorRole().name()),
                entity.getContent(),
                entity.getCreatedAt()
        );
    }

    public static TicketEntity toEntity(Ticket ticket, TicketEntity existing) {
        TicketEntity entity = existing != null ? existing
                : new TicketEntity(ticket.getId(), ticket.getTitle(), ticket.getDescription(),
                TicketEntity.PriorityJpa.valueOf(ticket.getPriority().name()),
                TicketEntity.StatusJpa.valueOf(ticket.getStatus().name()),
                ticket.getRequesterId(), ticket.getAssignedAgentId(),
                ticket.getCreatedAt(), ticket.getUpdatedAt());

        entity.setTitle(ticket.getTitle());
        entity.setDescription(ticket.getDescription());
        entity.setPriority(TicketEntity.PriorityJpa.valueOf(ticket.getPriority().name()));
        entity.setStatus(TicketEntity.StatusJpa.valueOf(ticket.getStatus().name()));
        entity.setAssignedAgentId(ticket.getAssignedAgentId());
        entity.setUpdatedAt(ticket.getUpdatedAt());

        int alreadyPersisted = entity.getComments().size();
        List<Comment> domainComments = ticket.getComments();
        for (int i = alreadyPersisted; i < domainComments.size(); i++) {
            Comment c = domainComments.get(i);
            entity.getComments().add(new CommentEntity(
                    c.getId(), entity, c.getAuthorId(),
                    CommentEntity.RoleJpa.valueOf(c.getAuthorRole().name()),
                    c.getContent(), c.getCreatedAt()
            ));
        }

        return entity;
    }
}