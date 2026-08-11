CREATE TABLE refresh_tokens (
                                id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                token        VARCHAR(200) NOT NULL,
                                user_id      UUID         NOT NULL REFERENCES users(id),
                                expires_at   TIMESTAMP    NOT NULL,
                                revoked      BOOLEAN      NOT NULL DEFAULT FALSE,

                                CONSTRAINT uq_refresh_tokens_token UNIQUE (token)
);

CREATE INDEX idx_refresh_token ON refresh_tokens(token);
CREATE INDEX idx_refresh_user  ON refresh_tokens(user_id);