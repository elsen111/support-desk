CREATE TABLE refresh_tokens (
                                id           UUID PRIMARY KEY,
                                user_id      UUID NOT NULL,
                                token_hash   VARCHAR(255) NOT NULL UNIQUE,
                                expires_at   TIMESTAMP NOT NULL,
                                revoked      BOOLEAN NOT NULL DEFAULT FALSE,
                                created_at   TIMESTAMP NOT NULL,
                                CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens (user_id);
CREATE INDEX idx_refresh_tokens_token_hash ON refresh_tokens (token_hash);