package com.supportdesk.domain.exception;

public class TicketAccessDeniedException extends DomainException {
    public TicketAccessDeniedException(String message) {
        super(message);
    }
}