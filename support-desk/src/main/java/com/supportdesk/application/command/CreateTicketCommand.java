package com.supportdesk.application.command;

import com.supportdesk.domain.enums.TicketPriority;
import com.supportdesk.domain.model.Actor;

public record CreateTicketCommand(
        Actor requester,
        String title,
        String description,
        TicketPriority priority
) {}