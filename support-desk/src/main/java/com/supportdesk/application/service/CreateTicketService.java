package com.supportdesk.application.service;

import com.supportdesk.application.command.CreateTicketCommand;
import com.supportdesk.application.port.in.CreateTicketUseCase;
import com.supportdesk.application.port.out.TicketRepositoryPort;
import com.supportdesk.domain.exception.TicketAccessDeniedException;
import com.supportdesk.domain.model.Ticket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

public class CreateTicketService implements CreateTicketUseCase {

    private static final Logger log = LoggerFactory.getLogger(AssignTicketService.class);

    private final TicketRepositoryPort ticketRepository;
    private final Clock clock;

    public CreateTicketService(TicketRepositoryPort ticketRepository, Clock clock) {
        this.ticketRepository = ticketRepository;
        this.clock = clock;
    }

    @Override
    public Ticket createTicket(CreateTicketCommand command) {
        if (!command.requester().isCustomer() && !command.requester().isAdmin()) {
            throw new TicketAccessDeniedException("Only customers or admins may create tickets");
        }
        Instant now = clock.instant();
        Ticket ticket = Ticket.create(
                UUID.randomUUID(),
                command.title(),
                command.description(),
                command.priority(),
                command.requester().userId(),
                now
        );

        Ticket saved = ticketRepository.save(ticket);

        log.info("Ticket {} created by requester {}", saved.getId(), command.requester().userId());

        return saved;
    }
}