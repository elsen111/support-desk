package com.supportdesk.application.command;

import com.supportdesk.domain.model.Actor;

import java.util.UUID;

public record AssignTicketCommand(
        Actor actor,
        UUID ticketId,
        UUID agentId
) {}