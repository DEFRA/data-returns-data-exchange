/*
 * DROP AND RECREATE THE data_returns_owner user.
 * 
 * This currently cannot be automated due to the default username/password restrictions for postgres.
 */
DROP OWNED BY data_returns_owner CASCADE;
DROP ROLE IF EXISTS data_returns_owner;
CREATE ROLE data_returns_owner LOGIN
	PASSWORD 'data_returns_owner'
  	SUPERUSER INHERIT CREATEDB CREATEROLE NOREPLICATION;