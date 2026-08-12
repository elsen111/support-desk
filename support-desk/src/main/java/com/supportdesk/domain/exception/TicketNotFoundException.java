package com.supportdesk.domain.exception;

import java.util.UUID;

public class TicketNotFoundException extends DomainException {
    public TicketNotFoundException(UUID ticketId) {
        super("Ticket not found: " + ticketId);
    }
}