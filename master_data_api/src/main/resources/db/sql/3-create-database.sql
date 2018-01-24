/*
 * CREATE THE DATA_RETURNS DATABASE
 */
CREATE DATABASE ${db.md_api.name} OWNER ${db.md_api.username} ENCODING 'UTF8';
GRANT ALL PRIVILEGES ON DATABASE ${db.md_api.name} TO ${db.md_api.username};
