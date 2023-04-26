BEGIN TRANSACTION;

DROP TABLE IF EXISTS transaction;
DROP TABLE IF EXISTS balance;
DROP TABLE IF EXISTS account;

CREATE TABLE account (
    id              BIGSERIAL PRIMARY KEY,
    customer_id     BIGINT NOT NULL,
    country         VARCHAR NOT NULL,
    created_on      TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE balance (
    id              BIGSERIAL PRIMARY KEY,
    amount          NUMERIC(32, 2) NOT NULL,
    account_id      BIGINT NOT NULL,
    currency        VARCHAR NOT NULL,
    created_on      TIMESTAMP WITH TIME ZONE NOT NULL,
    valid_from      TIMESTAMP WITH TIME ZONE NOT NULL,
    valid_to        TIMESTAMP WITH TIME ZONE,
    CONSTRAINT fk_account FOREIGN KEY(account_id) REFERENCES account(id)
);

CREATE TABLE transaction (
    id               BIGSERIAL PRIMARY KEY,
    amount           NUMERIC(32, 2) NOT NULL,
    description      VARCHAR NOT NULL,
    direction        VARCHAR NOT NULL,
    currency         VARCHAR NOT NULL,
    created_on       TIMESTAMP WITH TIME ZONE NOT NULL,
    account_id       BIGINT NOT NULL,
    balance_after    BIGINT NOT NULL,
    CONSTRAINT fk_account FOREIGN KEY(account_id) REFERENCES account(id),
    CONSTRAINT fk_new_balance FOREIGN KEY(balance_after) REFERENCES balance(id)
);

END TRANSACTION;
