// src/test/java/com/supportdesk/application/service/AddCommentServiceTest.java
package com.supportdesk.application.service;

import com.supportdesk.application.command.AddCommentCommand;
import com.supportdesk.application.port.out.TicketRepositoryPort;
import com.supportdesk.domain.enums.Role;
import com.supportdesk.domain.enums.TicketPriority;
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
class AddCommentServiceTest {

    @Mock
    private TicketRepositoryPort ticketRepository;

    private AddCommentService service;

    private final UUID ticketId = UUID.randomUUID();
    private final UUID requesterId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        Clock fixedClock = Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC);
        service = new AddCommentService(ticketRepository, fixedClock);
    }

    @Test
    void ownerCanCommentOnTheirTicket() {
        Ticket ticket = Ticket.create(ticketId, "Title", "Description", TicketPriority.LOW, requesterId, Instant.now());
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Actor owner = new Actor(requesterId, Role.CUSTOMER);
        Comment comment = service.addComment(new AddCommentCommand(owner, ticketId, "Any update on this?"));

        assertThat(comment.getContent()).isEqualTo("Any update on this?");
        assertThat(comment.getAuthorId()).isEqualTo(requesterId);
    }

    @Test
    void unrelatedCustomerCannotComment() {
        Ticket ticket = Ticket.create(ticketId, "Title", "Description", TicketPriority.LOW, requesterId, Instant.now());
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));

        Actor stranger = new Actor(UUID.randomUUID(), Role.CUSTOMER);

        assertThatThrownBy(() -> service.addComment(new AddCommentCommand(stranger, ticketId, "Hello")))
                .isInstanceOf(TicketAccessDeniedException.class);
    }
}