CREATE TABLE users (
                       id                          UUID PRIMARY KEY,
                       username                    VARCHAR(50)  NOT NULL,
                       email                       VARCHAR(255) NOT NULL,
                       password                    VARCHAR(255) NOT NULL,
                       role                        VARCHAR(20)  NOT NULL DEFAULT 'USER',
                       status                      VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
                       email_verified              BOOLEAN      NOT NULL DEFAULT FALSE,
                       verification_token          VARCHAR(100),
                       verification_token_expiry   TIMESTAMP,
                       failed_login_attempt        INTEGER      NOT NULL DEFAULT 0,
                       account_locked              BOOLEAN      NOT NULL DEFAULT FALSE,
                       locked_until                TIMESTAMP,
                       created_at                  TIMESTAMP    NOT NULL,
                       updated_at                  TIMESTAMP    NOT NULL,

                       CONSTRAINT uq_users_email UNIQUE (email)
);