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