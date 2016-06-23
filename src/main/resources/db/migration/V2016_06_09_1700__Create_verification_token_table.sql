CREATE SEQUENCE verification_token_id_seq;

CREATE TABLE verification_token (
    id BIGINT PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES app_user (id),
    token VARCHAR(100) NOT NULL,
    expires TIMESTAMP NOT NULL,
    
    date_created TIMESTAMP NOT NULL,
    last_updated TIMESTAMP NOT NULL
);