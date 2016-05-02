# Data Returns Back Office Server


## Running
The server JAR is executable.

### Profiles
The will run in standalone mode by default using the local filesystem for persistence.

To run in a cluster it is necessary to supply one of the appropriate profiles for the environment.

Current profiles are:
* debug - starts the server with increased logging
* cluster - starts the server in cluster mode (AWS S3 persistence)
* dev_cluster/test_cluster - starts the server in debug & cluster modes
* pre_prod - starts the server in cluster mode for pre-production environment
* production - starts the server in production mode

To specify a profile at start-up, add the spring.profiles.active argument, e.g.:

data-returns-data-exchange-1.0-SNAPSHOT.jar --spring.profiles.active=production