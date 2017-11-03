/*
 * CREATE THE DATA_RETURNS DATABASE
 */
CREATE DATABASE ${db.ecm_api.name} OWNER ${db.ecm_api.username} ENCODING 'UTF8';
GRANT ALL PRIVILEGES ON DATABASE ${db.ecm_api.name} TO ${db.ecm_api.username};
GRANT USAGE ON SCHEMA PUBLIC TO ${db.ecm_api.username};
GRANT CREATE ON SCHEMA PUBLIC TO ${db.ecm_api.username};
ALTER USER ${db.ecm_api.username} SET search_path = public;