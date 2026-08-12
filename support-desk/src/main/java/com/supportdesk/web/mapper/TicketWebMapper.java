package com.supportdesk.web.mapper;

import com.supportdesk.domain.model.Comment;
import com.supportdesk.domain.model.Ticket;
import com.supportdesk.web.dto.response.CommentResponse;
import com.supportdesk.web.dto.response.TicketResponse;

import java.util.List;
import java.util.stream.Collectors;

public final class TicketWebMapper {

    private TicketWebMapper() {}

    public static TicketResponse toResponse(Ticket ticket) {
        List<CommentResponse> comments = ticket.getComments().stream()
                .map(TicketWebMapper::toResponse)
                .collect(Collectors.toList());

        return new TicketResponse(
                ticket.getId(),
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getPriority().name(),
                ticket.getStatus().name(),
                ticket.getRequesterId(),
                ticket.getAssignedAgentId(),
                comments,
                ticket.getCreatedAt(),
                ticket.getUpdatedAt()
        );
    }

    public static CommentResponse toResponse(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getAuthorId(),
                comment.getAuthorRole().name(),
                comment.getContent(),
                comment.getCreatedAt()
        );
    }
}