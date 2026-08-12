package com.supportdesk.application.service;

import com.supportdesk.application.command.AddCommentCommand;
import com.supportdesk.application.port.in.AddCommentUseCase;
import com.supportdesk.application.port.out.TicketRepositoryPort;
import com.supportdesk.domain.exception.TicketNotFoundException;
import com.supportdesk.domain.model.Comment;
import com.supportdesk.domain.model.Ticket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

public class AddCommentService implements AddCommentUseCase {

    private static final Logger log = LoggerFactory.getLogger(AssignTicketService.class);

    private final TicketRepositoryPort ticketRepository;
    private final Clock clock;

    public AddCommentService(TicketRepositoryPort ticketRepository, Clock clock) {
        this.ticketRepository = ticketRepository;
        this.clock = clock;
    }

    @Override
    public Comment addComment(AddCommentCommand command) {
        Ticket ticket = ticketRepository.findById(command.ticketId())
                .orElseThrow(() -> new TicketNotFoundException(command.ticketId()));

        ticket.assertCommentableBy(command.actor());

        Instant now = clock.instant();
        Comment comment = Comment.create(
                UUID.randomUUID(),
                ticket.getId(),
                command.actor().userId(),
                command.actor().role(),
                command.content(),
                now
        );

        ticket.addComment(comment);
        ticketRepository.save(ticket);

        log.info("Comment {} added to ticket {} by actor {}", comment.getId(), ticket.getId(), command.actor().userId());

        return comment;
    }
}