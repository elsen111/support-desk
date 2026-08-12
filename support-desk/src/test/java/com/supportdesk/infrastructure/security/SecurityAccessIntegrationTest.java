// src/test/java/com/supportdesk/infrastructure/security/SecurityAccessIntegrationTest.java
package com.supportdesk.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supportdesk.web.dto.request.LoginRequest;
import com.supportdesk.web.dto.request.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityAccessIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private String customerToken;

    @BeforeEach
    void setUp() throws Exception {
        customerToken = registerAndLogin("customer_" + UUID.randomUUID(), "CUSTOMER");
    }

    private String registerAndLogin(String username, String role) throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RegisterRequest(username, "Password123!", role))))
                .andExpect(status().isCreated());

        String body = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest(username, "Password123!"))))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Map<?, ?> response = objectMapper.readValue(body, Map.class);
        Map<?, ?> data = (Map<?, ?>) response.get("data");
        return (String) data.get("token");
    }

    @Test
    void unauthenticatedRequestIsRejected() throws Exception {
        mockMvc.perform(get("/api/tickets"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void tokenWithBadSignatureIsRejected() throws Exception {
        mockMvc.perform(get("/api/tickets")
                        .header("Authorization", "Bearer " + customerToken + "tampered"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void authenticatedCustomerCanCreateTicket() throws Exception {
        mockMvc.perform(post("/api/tickets")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"title":"Cannot log in","description":"Getting 500 on login","priority":"HIGH"}
                            """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("OPEN"));
    }

    @Test
    void customerCannotAssignTicket() throws Exception {
        String createBody = mockMvc.perform(post("/api/tickets")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"title":"Cannot log in","description":"Getting 500 on login","priority":"HIGH"}
                            """))
                .andReturn().getResponse().getContentAsString();

        Map<?, ?> response = objectMapper.readValue(createBody, Map.class);
        Map<?, ?> data = (Map<?, ?>) response.get("data");
        String ticketId = (String) data.get("id");

        mockMvc.perform(post("/api/tickets/" + ticketId + "/assign")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"agentId\":\"" + UUID.randomUUID() + "\"}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("FORBIDDEN"));
    }

    @Test
    void duplicateUsernameRegistrationIsRejected() throws Exception {
        String username = "dup_" + UUID.randomUUID();
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RegisterRequest(username, "Password123!", "CUSTOMER"))))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RegisterRequest(username, "Password123!", "CUSTOMER"))))
                .andExpect(status().isConflict());
    }
}