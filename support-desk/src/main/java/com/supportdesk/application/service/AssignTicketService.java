// application/service/AssignTicketService.java  (updated with logging)
package com.supportdesk.application.service;

import com.supportdesk.application.command.AssignTicketCommand;
import com.supportdesk.application.port.in.AssignTicketUseCase;
import com.supportdesk.application.port.out.TicketRepositoryPort;
import com.supportdesk.application.port.out.UserDirectoryPort;
import com.supportdesk.domain.enums.Role;
import com.supportdesk.domain.exception.InvalidTicketDataException;
import com.supportdesk.domain.exception.TicketAccessDeniedException;
import com.supportdesk.domain.exception.TicketNotFoundException;
import com.supportdesk.domain.model.Ticket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;

public class AssignTicketService implements AssignTicketUseCase {

    private static final Logger log = LoggerFactory.getLogger(AssignTicketService.class);

    private final TicketRepositoryPort ticketRepository;
    private final UserDirectoryPort userDirectory;
    private final Clock clock;

    public AssignTicketService(TicketRepositoryPort ticketRepository, UserDirectoryPort userDirectory, Clock clock) {
        this.ticketRepository = ticketRepository;
        this.userDirectory = userDirectory;
        this.clock = clock;
    }

    @Override
    public Ticket assignTicket(AssignTicketCommand command) {
        if (!command.actor().isAdmin() && !command.actor().isAgent()) {
            log.warn("Actor {} denied: attempted to assign ticket {} without privilege",
                    command.actor().userId(), command.ticketId());
            throw new TicketAccessDeniedException("Only agents or admins may assign tickets");
        }

        Role assigneeRole = userDirectory.findRoleById(command.agentId())
                .orElseThrow(() -> new InvalidTicketDataException("Assignee does not exist: " + command.agentId()));
        if (assigneeRole != Role.AGENT) {
            throw new InvalidTicketDataException("Tickets may only be assigned to users with the AGENT role");
        }

        Ticket ticket = ticketRepository.findById(command.ticketId())
                .orElseThrow(() -> new TicketNotFoundException(command.ticketId()));

        ticket.assignTo(command.agentId(), clock.instant());
        Ticket saved = ticketRepository.save(ticket);
        log.info("Ticket {} assigned to agent {} by actor {}", saved.getId(), command.agentId(), command.actor().userId());
        return saved;
    }
}