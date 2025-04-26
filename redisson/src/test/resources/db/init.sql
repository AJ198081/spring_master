-- CREATE SCHEMA IF NOT EXISTS redisson;

-- ALTER SCHEMA redisson OWNER TO admin;

-- SET search_path TO redisson;

CREATE SEQUENCE product_id_seq START 1000 INCREMENT BY 100;

CREATE TABLE product
(
    id          BIGINT NOT NULL DEFAULT nextval('product_id_seq'),
    name        VARCHAR(50)    NOT NULL,
    description TEXT,
    price       NUMERIC(10, 2) NOT NULL
);

