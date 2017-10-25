/*
 * CREATE THE DATA_RETURNS DATABASE
 */
CREATE DATABASE ${db.name} OWNER ${db.username} ENCODING 'UTF8';
GRANT ALL PRIVILEGES ON DATABASE ${db.name} TO ${db.username};
GRANT USAGE ON SCHEMA PUBLIC TO ${db.username};
GRANT CREATE ON SCHEMA PUBLIC TO ${db.username};
ALTER USER ${db.username} SET search_path = public;