/*
 * CREATE THE DATA_RETURNS DATABASE
 */
CREATE DATABASE ${db.ecm_api.name} OWNER ${db.ecm_api.username} ENCODING 'UTF8';
GRANT ALL PRIVILEGES ON DATABASE ${db.ecm_api.name} TO ${db.ecm_api.username};
