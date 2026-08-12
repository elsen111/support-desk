package com.supportdesk.application.service;

import com.supportdesk.application.command.ChangeTicketStatusCommand;
import com.supportdesk.application.port.in.ChangeTicketStatusUseCase;
import com.supportdesk.application.port.out.TicketRepositoryPort;
import com.supportdesk.domain.enums.TicketStatus;
import com.supportdesk.domain.exception.TicketAccessDeniedException;
import com.supportdesk.domain.exception.TicketNotFoundException;
import com.supportdesk.domain.model.Ticket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;

public class ChangeTicketStatusService implements ChangeTicketStatusUseCase {

    private static final Logger log = LoggerFactory.getLogger(AssignTicketService.class);

    private final TicketRepositoryPort ticketRepository;
    private final Clock clock;

    public ChangeTicketStatusService(TicketRepositoryPort ticketRepository, Clock clock) {
        this.ticketRepository = ticketRepository;
        this.clock = clock;
    }

    @Override
    public Ticket changeStatus(ChangeTicketStatusCommand command) {
        Ticket ticket = ticketRepository.findById(command.ticketId())
                .orElseThrow(() -> new TicketNotFoundException(command.ticketId()));

        ticket.assertViewableBy(command.actor());

        boolean isPrivileged = command.actor().isAdmin() || command.actor().isAgent();
        boolean isCustomerReply = command.actor().isCustomer()
                && ticket.getStatus() == TicketStatus.WAITING_CUSTOMER
                && command.newStatus() == TicketStatus.IN_PROGRESS;

        if (!isPrivileged && !isCustomerReply) {
            throw new TicketAccessDeniedException("Actor may not change status of ticket " + ticket.getId());
        }

        ticket.changeStatus(command.newStatus(), clock.instant());

        Ticket saved = ticketRepository.save(ticket);

        log.info("Ticket {} status changed to {} by actor {}", saved.getId(), command.newStatus(), command.actor().userId());

        return saved;
    }
}