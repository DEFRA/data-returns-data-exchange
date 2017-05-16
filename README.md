# Data Returns Backend Service

RESTful service to support the submission and validation of Data Exchange Protocol (DEP) compliant data returns and their
transmission to downstream systems.

## Running

The service JAR is executable.  A management can be found in launches/aws/datareturns.sh

### Profiles

The will run in standalone mode by default using the local filesystem for persistence.

To run in a cluster it is necessary to supply one of the appropriate profiles for the environment.

Current profiles are

- **debug** - starts the service with increased logging
- **cluster** - starts the service in cluster mode (AWS S3 persistence)
- **dev_cluster/test_cluster** - starts the service in debug & cluster modes
- **pre_prod** - starts the service in cluster mode for pre-production environment
- **production** - starts the service in production mode

To specify a profile at start-up, add the `spring.profiles.active` argument, for example

```bash
data-returns-data-exchange-1.0-SNAPSHOT.jar --spring.profiles.active=production
```

## Contributing to this project

If you have an idea you'd like to contribute please log an issue.

All contributions should be submitted via a pull request.

## License

THIS INFORMATION IS LICENSED UNDER THE CONDITIONS OF THE OPEN GOVERNMENT LICENCE found at:

http://www.nationalarchives.gov.uk/doc/open-government-licence/version/3

The following attribution statement MUST be cited in your products and applications when using this information.

>Contains public sector information licensed under the Open Government license v3

### About the license

The Open Government Licence (OGL) was developed by the Controller of Her Majesty's Stationery Office (HMSO) to enable information providers in the public sector to license the use and re-use of their information under a common open licence.

It is designed to encourage use and re-use of information freely and flexibly, with only a few conditions.
