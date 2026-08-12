package com.supportdesk.application.port.in;

import com.supportdesk.application.command.AssignTicketCommand;
import com.supportdesk.domain.model.Ticket;
import org.springframework.stereotype.Service;

@Service
public interface AssignTicketUseCase {
    Ticket assignTicket(AssignTicketCommand command);
}