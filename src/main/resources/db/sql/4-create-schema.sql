/*
 * Create the schema for the data_returns database
 */
CREATE SCHEMA ${db.schema} AUTHORIZATION ${db.username};
ALTER USER ${db.username} SET search_path = ${db.schema};