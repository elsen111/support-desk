package com.supportdesk.application.port.in;

import com.supportdesk.application.command.CreateTicketCommand;
import com.supportdesk.domain.model.Ticket;
import org.springframework.stereotype.Service;

@Service
public interface CreateTicketUseCase {
    Ticket createTicket(CreateTicketCommand command);
}