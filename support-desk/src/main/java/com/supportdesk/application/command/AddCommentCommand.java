package com.supportdesk.application.command;

import com.supportdesk.domain.model.Actor;

import java.util.UUID;

public record AddCommentCommand(
        Actor actor,
        UUID ticketId,
        String content
) {}