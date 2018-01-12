/*
 * CREATE THE DATA_RETURNS DATABASE
 */
CREATE DATABASE ${db.pi_api.name} OWNER ${db.pi_api.username} ENCODING 'UTF8';
GRANT ALL PRIVILEGES ON DATABASE ${db.pi_api.name} TO ${db.pi_api.username};
GRANT USAGE ON SCHEMA PUBLIC TO ${db.pi_api.username};
GRANT CREATE ON SCHEMA PUBLIC TO ${db.pi_api.username};
ALTER USER ${db.pi_api.username} SET search_path = public;
