// web/controller/TicketController.java  (updated)
package com.supportdesk.web.controller;

import com.supportdesk.application.command.*;
import com.supportdesk.application.port.in.*;
import com.supportdesk.domain.enums.TicketPriority;
import com.supportdesk.domain.enums.TicketStatus;
import com.supportdesk.domain.model.Actor;
import com.supportdesk.domain.model.Ticket;
import com.supportdesk.infrastructure.security.CustomUserDetails;
import com.supportdesk.web.dto.request.AssignTicketRequest;
import com.supportdesk.web.dto.request.ChangeStatusRequest;
import com.supportdesk.web.dto.request.CreateTicketRequest;
import com.supportdesk.web.dto.response.ApiResponse;
import com.supportdesk.web.dto.response.TicketResponse;
import com.supportdesk.web.mapper.TicketWebMapper;
import com.supportdesk.web.support.ActorResolver;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tickets")
@Tag(name = "Tickets", description = "Ticket creation, assignment, status transitions and queries")
public class TicketController {

    private final CreateTicketUseCase createTicketUseCase;
    private final AssignTicketUseCase assignTicketUseCase;
    private final ChangeTicketStatusUseCase changeTicketStatusUseCase;
    private final TicketQueryUseCase ticketQueryUseCase;
    private final ActorResolver actorResolver;

    public TicketController(CreateTicketUseCase createTicketUseCase,
                            AssignTicketUseCase assignTicketUseCase,
                            ChangeTicketStatusUseCase changeTicketStatusUseCase,
                            TicketQueryUseCase ticketQueryUseCase,
                            ActorResolver actorResolver) {
        this.createTicketUseCase = createTicketUseCase;
        this.assignTicketUseCase = assignTicketUseCase;
        this.changeTicketStatusUseCase = changeTicketStatusUseCase;
        this.ticketQueryUseCase = ticketQueryUseCase;
        this.actorResolver = actorResolver;
    }

    @PostMapping
    @Operation(summary = "Create a new ticket (customers and admins)")
    public ResponseEntity<ApiResponse<TicketResponse>> create(@AuthenticationPrincipal CustomUserDetails principal,
                                                              @Valid @RequestBody CreateTicketRequest request) {
        Actor actor = actorResolver.resolve(principal);
        Ticket ticket = createTicketUseCase.createTicket(new CreateTicketCommand(
                actor, request.title(), request.description(), TicketPriority.valueOf(request.priority())
        ));
        return ResponseEntity.status(201).body(ApiResponse.success(TicketWebMapper.toResponse(ticket)));
    }

    @GetMapping("/{ticketId}")
    @Operation(summary = "Get a single ticket the caller is authorized to view")
    public ResponseEntity<ApiResponse<TicketResponse>> get(@AuthenticationPrincipal CustomUserDetails principal,
                                                           @PathVariable UUID ticketId) {
        Actor actor = actorResolver.resolve(principal);
        Ticket ticket = ticketQueryUseCase.getTicket(actor, ticketId);
        return ResponseEntity.ok(ApiResponse.success(TicketWebMapper.toResponse(ticket)));
    }

    @GetMapping
    @Operation(summary = "List tickets scoped to the caller's role (own tickets / queue / all)")
    public ResponseEntity<ApiResponse<List<TicketResponse>>> list(@AuthenticationPrincipal CustomUserDetails principal) {
        Actor actor = actorResolver.resolve(principal);
        List<TicketResponse> tickets = ticketQueryUseCase.listTicketsForActor(actor).stream()
                .map(TicketWebMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(tickets));
    }

    @PostMapping("/{ticketId}/assign")
    @Operation(summary = "Assign a ticket to an agent (agents and admins only)")
    public ResponseEntity<ApiResponse<TicketResponse>> assign(@AuthenticationPrincipal CustomUserDetails principal,
                                                              @PathVariable UUID ticketId,
                                                              @Valid @RequestBody AssignTicketRequest request) {
        Actor actor = actorResolver.resolve(principal);
        Ticket ticket = assignTicketUseCase.assignTicket(new AssignTicketCommand(actor, ticketId, request.agentId()));
        return ResponseEntity.ok(ApiResponse.success(TicketWebMapper.toResponse(ticket), "Ticket assigned"));
    }

    @PostMapping("/{ticketId}/status")
    @Operation(summary = "Transition a ticket's status through the finite state machine")
    public ResponseEntity<ApiResponse<TicketResponse>> changeStatus(@AuthenticationPrincipal CustomUserDetails principal,
                                                                    @PathVariable UUID ticketId,
                                                                    @Valid @RequestBody ChangeStatusRequest request) {
        Actor actor = actorResolver.resolve(principal);
        Ticket ticket = changeTicketStatusUseCase.changeStatus(new ChangeTicketStatusCommand(
                actor, ticketId, TicketStatus.valueOf(request.status())
        ));
        return ResponseEntity.ok(ApiResponse.success(TicketWebMapper.toResponse(ticket), "Status updated"));
    }
}