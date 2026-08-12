package com.supportdesk.application.port.in;

import com.supportdesk.domain.model.Actor;
import com.supportdesk.domain.model.Ticket;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface TicketQueryUseCase {
    Ticket getTicket(Actor actor, UUID ticketId);
    List<Ticket> listTicketsForActor(Actor actor);
}