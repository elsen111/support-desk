package com.supportdesk.infrastructure.persistence.repository;

import com.supportdesk.infrastructure.persistence.entity.TicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TicketJpaRepository extends JpaRepository<TicketEntity, UUID> {
    List<TicketEntity> findByRequesterId(UUID requesterId);
    List<TicketEntity> findByAssignedAgentId(UUID assignedAgentId);
}