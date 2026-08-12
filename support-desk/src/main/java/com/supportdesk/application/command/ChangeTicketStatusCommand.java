package com.supportdesk.application.command;

import com.supportdesk.domain.enums.TicketStatus;
import com.supportdesk.domain.model.Actor;

import java.util.UUID;

public record ChangeTicketStatusCommand(
        Actor actor,
        UUID ticketId,
        TicketStatus newStatus
) {}