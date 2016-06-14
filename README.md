# Data Returns Backend Service
RESTful service to support the upload and validation of DEP compliant data returns.
This service handles the validation and preparation of uploaded files for submission
to the downstream persistence provider.

## Running
The service JAR is executable.  A management can be found in launches/aws/datareturns.sh

### Profiles
The will run in standalone mode by default using the local filesystem for persistence.

To run in a cluster it is necessary to supply one of the appropriate profiles for the environment.

Current profiles are:
* debug - starts the service with increased logging
* cluster - starts the service in cluster mode (AWS S3 persistence)
* dev_cluster/test_cluster - starts the service in debug & cluster modes
* pre_prod - starts the service in cluster mode for pre-production environment
* production - starts the service in production mode

To specify a profile at start-up, add the spring.profiles.active argument, e.g.:

data-returns-data-exchange-1.0-SNAPSHOT.jar --spring.profiles.active=production