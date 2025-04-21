CREATE SCHEMA IF NOT EXISTS auth;
CREATE SCHEMA IF NOT EXISTS e_commerce;

ALTER SCHEMA e_commerce OWNER TO admin;
ALTER SCHEMA auth OWNER TO admin;

SET search_path TO e_commerce, auth;
