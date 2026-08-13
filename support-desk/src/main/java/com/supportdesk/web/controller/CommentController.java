package com.supportdesk.web.controller;

import com.supportdesk.application.command.AddCommentCommand;
import com.supportdesk.application.port.in.AddCommentUseCase;
import com.supportdesk.domain.model.Actor;
import com.supportdesk.domain.model.Comment;
import com.supportdesk.infrastructure.security.CustomUserDetails;
import com.supportdesk.web.dto.request.AddCommentRequest;
import com.supportdesk.web.dto.response.ApiResponse;
import com.supportdesk.web.dto.response.CommentResponse;
import com.supportdesk.web.mapper.TicketWebMapper;
import com.supportdesk.web.support.ActorResolver;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/tickets/{ticketId}/comments")
@Tag(name = "Comments", description = "Collaborative comments scoped to a single ticket")
@SecurityRequirement(name = "bearerAuth")
public class CommentController {

    private final AddCommentUseCase addCommentUseCase;
    private final ActorResolver actorResolver;

    public CommentController(AddCommentUseCase addCommentUseCase, ActorResolver actorResolver) {
        this.addCommentUseCase = addCommentUseCase;
        this.actorResolver = actorResolver;
    }

    @PostMapping
    @Operation(summary = "Add a comment to a ticket the caller can access")
    public ResponseEntity<ApiResponse<CommentResponse>> add(@AuthenticationPrincipal CustomUserDetails principal,
                                                            @PathVariable UUID ticketId,
                                                            @Valid @RequestBody AddCommentRequest request) {
        Actor actor = actorResolver.resolve(principal);
        Comment comment = addCommentUseCase.addComment(new AddCommentCommand(actor, ticketId, request.content()));
        return ResponseEntity.status(201).body(ApiResponse.success(TicketWebMapper.toResponse(comment),
                "Comment has been successfully added."));
    }
}