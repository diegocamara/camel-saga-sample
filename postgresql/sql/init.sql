CREATE DATABASE paymentsdb;
CREATE DATABASE flightdb;
CREATE DATABASE hoteldb;

CREATE USER payments_service;
CREATE USER flight_service;
CREATE USER hotel_service;

ALTER USER payments_service WITH PASSWORD 'payments_service_123';
GRANT ALL PRIVILEGES ON DATABASE paymentsdb TO payments_service;

ALTER USER flight_service WITH PASSWORD 'flight_service_123';
GRANT ALL PRIVILEGES ON DATABASE flightdb TO flight_service;

ALTER USER hotel_service WITH PASSWORD 'hotel_service_123';
GRANT ALL PRIVILEGES ON DATABASE hoteldb TO hotel_service;

\c paymentsdb;

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

GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO payments_service;

INSERT INTO accounts VALUES ('2aa2ae05-3761-4d4a-926b-6a27031192e5', 0, 100);

\c flightdb;

CREATE TABLE tickets (
    id UUID NOT NULL,
    location_from VARCHAR(255) NOT NULL,
    location_destination VARCHAR(255) NOT NULL,
    CONSTRAINT tickets_pk PRIMARY KEY (id)
);

CREATE TABLE ticket_customer (
    ticket_id UUID NOT NULL,
    customer_id UUID NOT NULL,
    CONSTRAINT ticket_customer_pk PRIMARY KEY (ticket_id, customer_id),
    CONSTRAINT ticket_customer_ticket_fk FOREIGN KEY (ticket_id) REFERENCES tickets (id)
);

CREATE TABLE operations (
    id UUID NOT NULL,
    operation_name VARCHAR(255) NOT NULL,
    CONSTRAINT operations_pk PRIMARY KEY (id)
);

\c hoteldb;

CREATE TABLE bedrooms (
    id UUID NOT NULL,
    CONSTRAINT bedrooms_pk PRIMARY KEY (id)
);

CREATE TABLE booking (
    id UUID NOT NULL,
    bedroom_id UUID NOT NULL,
    customer_id UUID NOT NULL,
    period_from TIMESTAMP NOT NULL,
    period_to TIMESTAMP NOT NULL,
    CONSTRAINT booking_pk PRIMARY KEY (id),
    CONSTRAINT booking_bedrooms_fk FOREIGN KEY (bedroom_id) REFERENCES bedrooms (id)
);

CREATE TABLE operations (
    id UUID NOT NULL,
    operation_name VARCHAR(255) NOT NULL,
    CONSTRAINT operations_pk PRIMARY KEY (id)
);



