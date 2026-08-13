package com.supportdesk.infrastructure.persistence.adapter;

import com.supportdesk.application.port.out.TicketRepositoryPort;
import com.supportdesk.domain.model.Ticket;
import com.supportdesk.infrastructure.persistence.entity.TicketEntity;
import com.supportdesk.infrastructure.persistence.mapper.TicketPersistenceMapper;
import com.supportdesk.infrastructure.persistence.repository.TicketJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class TicketRepositoryAdapter implements TicketRepositoryPort {

    private final TicketJpaRepository jpaRepository;

    public TicketRepositoryAdapter(TicketJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Ticket save(Ticket ticket) {
        TicketEntity existing = jpaRepository.findById(ticket.getId()).orElse(null);
        TicketEntity entity = TicketPersistenceMapper.toEntity(ticket, existing);
        TicketEntity saved = jpaRepository.save(entity);
        return TicketPersistenceMapper.toDomain(saved);
    }

    @Override
    public Optional<Ticket> findById(UUID id) {
        return jpaRepository.findById(id).map(TicketPersistenceMapper::toDomain);
    }

    @Override
    public List<Ticket> findByRequesterId(UUID requesterId) {
        return jpaRepository.findByRequesterId(requesterId).stream()
                .map(TicketPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Ticket> findByAssignedAgentId(UUID agentId) {
        return jpaRepository.findByAssignedAgentId(agentId).stream()
                .map(TicketPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Ticket> findAll() {
        return jpaRepository.findAll().stream()
                .map(TicketPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }
}