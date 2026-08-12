package com.supportdesk.application.port.out;

import com.supportdesk.domain.model.Ticket;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TicketRepositoryPort {
    Ticket save(Ticket ticket);
    Optional<Ticket> findById(UUID id);
    List<Ticket> findByRequesterId(UUID requesterId);
    List<Ticket> findByAssignedAgentId(UUID agentId);
    List<Ticket> findAll();
}