CREATE TABLE accounts (
    id UUID NOT NULL,
    used NUMERIC NOT NULL,
    max_limit NUMERIC NOT NULL,
    CONSTRAINT accounts_pk PRIMARY KEY (id)
);

CREATE TABLE operations (
    id UUID NOT NULL,
    operation_name VARCHAR(255) NOT NULL,
    CONSTRAINT operations_pk PRIMARY KEY (id)
);