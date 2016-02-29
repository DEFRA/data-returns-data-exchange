/*
 * Create the schema for the data_returns database
 */
CREATE SCHEMA data_returns_schema AUTHORIZATION data_returns_owner;
ALTER USER data_returns_owner SET search_path = data_returns_schema;