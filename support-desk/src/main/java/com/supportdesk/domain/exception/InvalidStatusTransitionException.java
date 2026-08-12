package com.supportdesk.domain.exception;

import com.supportdesk.domain.enums.TicketStatus;

public class InvalidStatusTransitionException extends DomainException {
    public InvalidStatusTransitionException(TicketStatus from, TicketStatus to) {
        super("Cannot transition ticket from " + from + " to " + to);
    }
}