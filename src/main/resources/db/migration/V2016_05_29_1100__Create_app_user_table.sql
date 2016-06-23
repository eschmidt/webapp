CREATE SEQUENCE app_user_id_seq;

CREATE TABLE app_user (
    id BIGINT PRIMARY KEY,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT 'false',
    
    date_created TIMESTAMP NOT NULL,
    last_updated TIMESTAMP NOT NULL
);