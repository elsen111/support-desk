package com.supportdesk.config;

import com.supportdesk.application.port.in.*;
import com.supportdesk.application.port.out.TicketRepositoryPort;
import com.supportdesk.application.port.out.UserDirectoryPort;
import com.supportdesk.application.service.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class UseCaseConfig {

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    public CreateTicketUseCase createTicketUseCase(TicketRepositoryPort ticketRepository, Clock clock) {
        return new CreateTicketService(ticketRepository, clock);
    }

    @Bean
    public AssignTicketUseCase assignTicketUseCase(TicketRepositoryPort ticketRepository,
                                                   UserDirectoryPort userDirectory, Clock clock) {
        return new AssignTicketService(ticketRepository, userDirectory, clock);
    }

    @Bean
    public AddCommentUseCase addCommentUseCase(TicketRepositoryPort ticketRepository, Clock clock) {
        return new AddCommentService(ticketRepository, clock);
    }

    @Bean
    public ChangeTicketStatusUseCase changeTicketStatusUseCase(TicketRepositoryPort ticketRepository, Clock clock) {
        return new ChangeTicketStatusService(ticketRepository, clock);
    }

    @Bean
    public TicketQueryUseCase ticketQueryUseCase(TicketRepositoryPort ticketRepository) {
        return new TicketQueryService(ticketRepository);
    }
}