package com.supportdesk.domain.exception;

public class InvalidTicketDataException extends DomainException {
    public InvalidTicketDataException(String message) {
        super(message);
    }
}