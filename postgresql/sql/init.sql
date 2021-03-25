CREATE USER accounts_service;
ALTER USER accounts_service WITH PASSWORD 'accounts_service_123';
CREATE DATABASE accountsdb;
GRANT ALL PRIVILEGES ON DATABASE accountsdb TO accounts_service;

\c accountsdb;

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


INSERT INTO accounts VALUES ('2aa2ae05-3761-4d4a-926b-6a27031192e5', 0, 100);