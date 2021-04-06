CREATE TABLE tickets (
    id UUID NOT NULL,
    price NUMERIC NOT NULL,
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
    status VARCHAR(255) NOT NULL,
    output_field VARCHAR(512) NOT NULL,
    CONSTRAINT operations_pk PRIMARY KEY (id)
);