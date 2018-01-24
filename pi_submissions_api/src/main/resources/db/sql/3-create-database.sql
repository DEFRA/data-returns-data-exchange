/*
 * CREATE THE DATA_RETURNS DATABASE
 */
CREATE DATABASE ${db.pi_api.name} OWNER ${db.pi_api.username} ENCODING 'UTF8';
GRANT ALL PRIVILEGES ON DATABASE ${db.pi_api.name} TO ${db.pi_api.username};
