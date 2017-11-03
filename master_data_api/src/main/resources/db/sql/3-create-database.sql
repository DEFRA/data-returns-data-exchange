/*
 * CREATE THE DATA_RETURNS DATABASE
 */
CREATE DATABASE ${db.md_api.name} OWNER ${db.md_api.username} ENCODING 'UTF8';
GRANT ALL PRIVILEGES ON DATABASE ${db.md_api.name} TO ${db.md_api.username};
GRANT USAGE ON SCHEMA PUBLIC TO ${db.md_api.username};
GRANT CREATE ON SCHEMA PUBLIC TO ${db.md_api.username};
ALTER USER ${db.md_api.username} SET search_path = public;