/*
 * DROP AND RECREATE THE DATABASE USER.
 * 
 * This currently cannot be automated due to the default username/password restrictions for postgres.
 */
DROP OWNED BY ${db.username} CASCADE;
DROP ROLE IF EXISTS ${db.username};
CREATE ROLE ${db.username} LOGIN
	PASSWORD '${db.password}'
  	SUPERUSER INHERIT CREATEDB CREATEROLE NOREPLICATION;