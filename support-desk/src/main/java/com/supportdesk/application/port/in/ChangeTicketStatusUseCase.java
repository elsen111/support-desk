package com.supportdesk.application.port.in;

import com.supportdesk.application.command.ChangeTicketStatusCommand;
import com.supportdesk.domain.model.Ticket;
import org.springframework.stereotype.Service;

@Service
public interface ChangeTicketStatusUseCase {
    Ticket changeStatus(ChangeTicketStatusCommand command);
}