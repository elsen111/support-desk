package com.supportdesk.domain.exception;

public class UnauthorizedTicketOperationException extends RuntimeException {
    public UnauthorizedTicketOperationException(String message) {
        super(message);
    }
}
