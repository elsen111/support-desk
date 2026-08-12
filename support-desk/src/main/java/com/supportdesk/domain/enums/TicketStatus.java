package com.supportdesk.domain.enums;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public enum TicketStatus {
    OPEN,
    IN_PROGRESS,
    WAITING_CUSTOMER,
    RESOLVED,
    CLOSED;

    private static final Map<TicketStatus, Set<TicketStatus>> ALLOWED_TRANSITIONS = new EnumMap<>(TicketStatus.class);

    static {
        ALLOWED_TRANSITIONS.put(OPEN, EnumSet.of(IN_PROGRESS, CLOSED));
        ALLOWED_TRANSITIONS.put(IN_PROGRESS, EnumSet.of(WAITING_CUSTOMER, RESOLVED, CLOSED));
        ALLOWED_TRANSITIONS.put(WAITING_CUSTOMER, EnumSet.of(IN_PROGRESS, CLOSED));
        ALLOWED_TRANSITIONS.put(RESOLVED, EnumSet.of(IN_PROGRESS, CLOSED));
        ALLOWED_TRANSITIONS.put(CLOSED, EnumSet.noneOf(TicketStatus.class));
    }

    public boolean canTransitionTo(TicketStatus target) {
        return ALLOWED_TRANSITIONS.get(this).contains(target);
    }

    public boolean isTerminal() {
        return this == CLOSED;
    }
}