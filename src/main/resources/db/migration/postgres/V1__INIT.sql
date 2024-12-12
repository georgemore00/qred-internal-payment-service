-- CLIENT TABLE
CREATE TABLE client
(
    id         BIGINT NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name  VARCHAR(255) NOT NULL,
    CONSTRAINT pk_client PRIMARY KEY (id)
);

-- CONTRACT TABLE
CREATE TABLE contract
(
    id               BIGINT       NOT NULL,
    client_id BIGINT,
    contract_number  VARCHAR(255) UNIQUE NOT NULL,
    CONSTRAINT pk_contract PRIMARY KEY (id)
);

ALTER TABLE contract
    ADD CONSTRAINT fk_contract_on_client FOREIGN KEY (client_id) REFERENCES client (id);

-- PAYMENT TABLE
CREATE SEQUENCE IF NOT EXISTS payment_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE payment
(
    id                 BIGINT         NOT NULL,
    payment_date       date           NOT NULL,
    amount             DECIMAL(19, 2) NOT NULL,
    payment_type       VARCHAR(255)   NOT NULL,
    contract_id BIGINT,
    CONSTRAINT pk_payment PRIMARY KEY (id)
);

ALTER TABLE payment
    ADD CONSTRAINT fk_payment_on_contract FOREIGN KEY (contract_id) REFERENCES contract (id);