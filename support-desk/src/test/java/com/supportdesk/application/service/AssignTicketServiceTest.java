package com.supportdesk.application.service;

import com.supportdesk.application.command.AssignTicketCommand;
import com.supportdesk.application.port.out.TicketRepositoryPort;
import com.supportdesk.application.port.out.UserDirectoryPort;
import com.supportdesk.domain.enums.Role;
import com.supportdesk.domain.enums.TicketPriority;
import com.supportdesk.domain.enums.TicketStatus;
import com.supportdesk.domain.exception.InvalidTicketDataException;
import com.supportdesk.domain.exception.TicketAccessDeniedException;
import com.supportdesk.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssignTicketServiceTest {

    @Mock
    private TicketRepositoryPort ticketRepository;

    @Mock
    private UserDirectoryPort userDirectory;

    private AssignTicketService service;

    private final UUID ticketId = UUID.randomUUID();
    private final UUID agentId = UUID.randomUUID();
    private final UUID requesterId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        Clock fixedClock = Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC);
        service = new AssignTicketService(ticketRepository, userDirectory, fixedClock);
    }

    private Ticket sampleTicket() {
        return Ticket.create(ticketId, "Title", "Description", TicketPriority.HIGH, requesterId, Instant.now());
    }

    @Test
    void assignsTicketWhenActorIsAgentAndAssigneeIsValidAgent() {
        Actor actingAgent = new Actor(UUID.randomUUID(), Role.AGENT);
        when(userDirectory.findRoleById(agentId)).thenReturn(Optional.of(Role.AGENT));
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(sampleTicket()));
        when(ticketRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Ticket result = service.assignTicket(new AssignTicketCommand(actingAgent, ticketId, agentId));

        assertThat(result.getAssignedAgentId()).isEqualTo(agentId);
        assertThat(result.getStatus()).isEqualTo(TicketStatus.IN_PROGRESS);
    }

    @Test
    void rejectsAssignmentByCustomer() {
        Actor customer = new Actor(UUID.randomUUID(), Role.CUSTOMER);

        assertThatThrownBy(() -> service.assignTicket(new AssignTicketCommand(customer, ticketId, agentId)))
                .isInstanceOf(TicketAccessDeniedException.class);
    }

    @Test
    void rejectsAssignmentToNonAgentUser() {
        Actor admin = new Actor(UUID.randomUUID(), Role.ADMIN);
        when(userDirectory.findRoleById(agentId)).thenReturn(Optional.of(Role.CUSTOMER));

        assertThatThrownBy(() -> service.assignTicket(new AssignTicketCommand(admin, ticketId, agentId)))
                .isInstanceOf(InvalidTicketDataException.class);
    }
}