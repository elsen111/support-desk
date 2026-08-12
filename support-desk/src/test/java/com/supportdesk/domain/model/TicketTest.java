package com.supportdesk.domain.model;

import com.supportdesk.domain.enums.Role;
import com.supportdesk.domain.enums.TicketPriority;
import com.supportdesk.domain.enums.TicketStatus;
import com.supportdesk.domain.exception.InvalidStatusTransitionException;
import com.supportdesk.domain.exception.InvalidTicketDataException;
import com.supportdesk.domain.exception.TicketAccessDeniedException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TicketTest {

    private final UUID requesterId = UUID.randomUUID();
    private final Instant now = Instant.now();

    @Test
    void createsTicketInOpenStatus() {
        Ticket ticket = Ticket.create(UUID.randomUUID(), "Login broken", "Cannot log in", TicketPriority.HIGH, requesterId, now);

        assertThat(ticket.getStatus()).isEqualTo(TicketStatus.OPEN);
        assertThat(ticket.getAssignedAgentId()).isNull();
    }

    @Test
    void rejectsBlankTitle() {
        assertThatThrownBy(() -> Ticket.create(UUID.randomUUID(), "  ", "desc", TicketPriority.LOW, requesterId, now))
                .isInstanceOf(InvalidTicketDataException.class);
    }

    @Test
    void assigningMovesOpenTicketToInProgress() {
        Ticket ticket = Ticket.create(UUID.randomUUID(), "Title", "Description", TicketPriority.MEDIUM, requesterId, now);
        UUID agentId = UUID.randomUUID();

        ticket.assignTo(agentId, now);

        assertThat(ticket.getAssignedAgentId()).isEqualTo(agentId);
        assertThat(ticket.getStatus()).isEqualTo(TicketStatus.IN_PROGRESS);
    }

    @Test
    void allowsValidStatusTransition() {
        Ticket ticket = Ticket.create(UUID.randomUUID(), "Title", "Description", TicketPriority.MEDIUM, requesterId, now);
        ticket.assignTo(UUID.randomUUID(), now);

        ticket.changeStatus(TicketStatus.WAITING_CUSTOMER, now);

        assertThat(ticket.getStatus()).isEqualTo(TicketStatus.WAITING_CUSTOMER);
    }

    @Test
    void rejectsInvalidStatusTransition() {
        Ticket ticket = Ticket.create(UUID.randomUUID(), "Title", "Description", TicketPriority.MEDIUM, requesterId, now);

        assertThatThrownBy(() -> ticket.changeStatus(TicketStatus.RESOLVED, now))
                .isInstanceOf(InvalidStatusTransitionException.class);
    }

    @Test
    void closedTicketIsTerminal() {
        Ticket ticket = Ticket.create(UUID.randomUUID(), "Title", "Description", TicketPriority.MEDIUM, requesterId, now);
        ticket.changeStatus(TicketStatus.CLOSED, now);

        assertThatThrownBy(() -> ticket.changeStatus(TicketStatus.IN_PROGRESS, now))
                .isInstanceOf(InvalidStatusTransitionException.class);
    }

    @Test
    void ownerCanViewTicket() {
        Ticket ticket = Ticket.create(UUID.randomUUID(), "Title", "Description", TicketPriority.LOW, requesterId, now);
        Actor owner = new Actor(requesterId, Role.CUSTOMER);

        ticket.assertViewableBy(owner);
    }

    @Test
    void unrelatedCustomerCannotViewTicket() {
        Ticket ticket = Ticket.create(UUID.randomUUID(), "Title", "Description", TicketPriority.LOW, requesterId, now);
        Actor stranger = new Actor(UUID.randomUUID(), Role.CUSTOMER);

        assertThatThrownBy(() -> ticket.assertViewableBy(stranger))
                .isInstanceOf(TicketAccessDeniedException.class);
    }

    @Test
    void adminCanAlwaysView() {
        Ticket ticket = Ticket.create(UUID.randomUUID(), "Title", "Description", TicketPriority.LOW, requesterId, now);
        Actor admin = new Actor(UUID.randomUUID(), Role.ADMIN);

        ticket.assertViewableBy(admin);
    }
}