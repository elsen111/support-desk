package com.supportdesk.application.service;

import com.supportdesk.application.port.in.TicketQueryUseCase;
import com.supportdesk.application.port.out.TicketRepositoryPort;
import com.supportdesk.domain.model.Actor;
import com.supportdesk.domain.model.Ticket;

import java.util.List;
import java.util.UUID;

public class TicketQueryService implements TicketQueryUseCase {

    private final TicketRepositoryPort ticketRepository;

    public TicketQueryService(TicketRepositoryPort ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Override
    public Ticket getTicket(Actor actor, UUID ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new com.supportdesk.domain.exception.TicketNotFoundException(ticketId));
        ticket.assertViewableBy(actor);
        return ticket;
    }

    @Override
    public List<Ticket> listTicketsForActor(Actor actor) {
        if (actor.isAdmin()) {
            return ticketRepository.findAll();
        }
        if (actor.isAgent()) {
            return ticketRepository.findByAssignedAgentId(actor.userId());
        }
        return ticketRepository.findByRequesterId(actor.userId());
    }
}