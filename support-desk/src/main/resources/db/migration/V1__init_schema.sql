CREATE TABLE users (
                       id             UUID PRIMARY KEY,
                       username       VARCHAR(100) NOT NULL UNIQUE,
                       password_hash  VARCHAR(255) NOT NULL,
                       role           VARCHAR(20)  NOT NULL CHECK (role IN ('CUSTOMER', 'AGENT', 'ADMIN'))
);

CREATE TABLE tickets (
                         id                 UUID PRIMARY KEY,
                         title              VARCHAR(200)  NOT NULL,
                         description        VARCHAR(5000) NOT NULL,
                         priority           VARCHAR(20)   NOT NULL CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH', 'URGENT')),
                         status             VARCHAR(20)   NOT NULL CHECK (status IN ('OPEN', 'IN_PROGRESS', 'WAITING_CUSTOMER', 'RESOLVED', 'CLOSED')),
                         requester_id       UUID NOT NULL REFERENCES users (id),
                         assigned_agent_id  UUID REFERENCES users (id),
                         created_at         TIMESTAMP NOT NULL,
                         updated_at         TIMESTAMP NOT NULL,
                         version            BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_tickets_requester_id ON tickets (requester_id);
CREATE INDEX idx_tickets_assigned_agent_id ON tickets (assigned_agent_id);
CREATE INDEX idx_tickets_status ON tickets (status);

CREATE TABLE comments (
                          id           UUID PRIMARY KEY,
                          ticket_id    UUID NOT NULL REFERENCES tickets (id) ON DELETE CASCADE,
                          author_id    UUID NOT NULL REFERENCES users (id),
                          author_role  VARCHAR(20) NOT NULL CHECK (author_role IN ('CUSTOMER', 'AGENT', 'ADMIN')),
                          content      VARCHAR(2000) NOT NULL,
                          created_at   TIMESTAMP NOT NULL
);

CREATE INDEX idx_comments_ticket_id ON comments (ticket_id);