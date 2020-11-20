DROP TABLE IF EXISTS transaction;
DROP TABLE IF EXISTS account_balance;
DROP TABLE IF EXISTS currency;
DROP TABLE IF EXISTS account;
DROP TABLE IF EXISTS customer;

CREATE TABLE customer (
    id VARCHAR(255) DEFAULT gen_random_uuid()::varchar primary key
);

CREATE TABLE currency (
    code VARCHAR(3) NOT NULL primary key
);

CREATE TABLE account (
   id VARCHAR(255) DEFAULT gen_random_uuid()::varchar primary key,
   customer_id VARCHAR(255) NOT NULL,
   country VARCHAR(255) NOT NULL,
       CONSTRAINT fk_customer
       FOREIGN KEY(customer_id)
       REFERENCES customer(id)
);

CREATE TABLE account_balance (
    account_id VARCHAR(255) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    amount NUMERIC(10,2) NOT NULL,
       CONSTRAINT fk_account
       FOREIGN KEY(account_id)
       REFERENCES account(id),
          CONSTRAINT fk_currency
          FOREIGN KEY(currency)
          REFERENCES currency(code)
 );

 CREATE TABLE transaction (
    id VARCHAR(255) DEFAULT gen_random_uuid()::varchar primary key,
    account_id VARCHAR(255) NOT NULL,
    amount NUMERIC(10,2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    direction VARCHAR(3) NOT NULL,
    description VARCHAR(255) NOT NULL,
    balance NUMERIC(10,2) NOT NULL,
           CONSTRAINT fk_transaction_account
           FOREIGN KEY(account_id)
           REFERENCES account(id),
              CONSTRAINT fk_transaction_currency
              FOREIGN KEY(currency)
              REFERENCES currency(code)
 );


INSERT INTO customer VALUES (DEFAULT);

INSERT INTO currency VALUES ('EUR');
INSERT INTO currency VALUES ('SEK');
INSERT INTO currency VALUES ('GBP');
INSERT INTO currency VALUES ('USD');

SELECT id AS CUSTOMER_ID FROM customer;