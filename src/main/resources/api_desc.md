The Environment Agency Data Returns API allows for the management and
submission of environmental data.

```
******    THIS DOCUMENT IS WORK IN PROGRESS    ******
```

## NOTES FOR THE (INTERNAL) READER
### Notes/todo's/brain dump on the design/implementation:
- #### Feedback/Questions
  - Need to consider how this API will be published?
      - Will it be independent from the frontend (as in, a completely
      separate AWS environment/project)
      - The current service is published under
      report-landfill-data.service.gov.uk however this API will not be
      sector specific.
  - Need to discuss and refine terminology.  Dataset->Record->Payload
  probably not very good (and not focused at the target audience),
  perhaps Submission->Entries->Data?
  - The design of this API has highlighted the need to address some of
  the previously identified shortcomings of the DEP which were driven
  from a lack of proper support in the Emma system.
    - Mon_Date does not support a proper ISO8601/RFC3339 compliant date and
     therefore we cannot use the proper 'date-time' data-type to represent
     it
    - The (numeric) Value field is actually a string type as the DEP states
    that it can specify a range by preceding the numeric value with a < or >.
    This is not great from a data modelling perspective.  Thoughts:
      - we certainly shouldn't store it like this in the database (as it
      isn't query-able as a numeric type)
      - I don't think the API should operate in this way either (split the the
      two fields out)
      - should we consider revising the DEP to improve the quality of
      the model?
    - Rtn_Period is somewhat meaningless from a data perspective.  User
    research has already highlighted the ambiguity of "Qtr 1".  Databases,
    API's, systems in general, only work with appropriate data-types......
  - Should datasets still be accessible once they have been submitted to
  the EA?
    - If so, for how long?
  - How long should a unsubmitted dataset be allowed to exist?
  - No such thing as unlimited storage, do we need to impose a limit on the
  number of unsubmitted datasets and/or the number of requests that can be
  added to a dataset?
- #### TODO
  - Rework PUT/POST method definitions to allow for submission of multiple
  requests in one call
    - https://developers.facebook.com/docs/graph-api/making-multiple-requests
  - Determine how "allowed values" search/filter functionality should work
   as the frontend has different requirements for different list types
   (permit lookup tool vs the controlled lists) yet the API should provide a
   consistent search/filter functionality regardless of the list type.  The
   frontend will have to make a slightly different API call for the
   different list types to maintain the current functionality.
  - Refine data model
  - Document best practices that API consumers should use e.g. using the
  conditional requests functionality to avoid downloading controlled list
  definitions all the time.

  - Define security model (OAuth-2 via spring security -
  https://gdstechnology.blog.gov.uk/2016/11/14/our-approach-to-authentication/)
    - How do we manage users?  GAP project?  Implement central EA service
    which could be adapted to act as a facade for GAP service later?
    - Consider if multiple users should have shared access to a dataset?
  - Rate limiting/Throttling
    - Determine appropriate practice, one possibility is to use a `503
    Service Unavailable` responses however I think `429 Too
    Many Requests` (RFC 6585) may be a better approach (4xx indicates a
    client error, 5xx indicates server error.  Making too many requests IS
    a client error!)
    - Consider whitelisting certain hosts?  E.g. the frontend, large
    operators?
  - Determine storage medium for unsubmitted (and potentially invalid)
  data
    - Potentially could use redis or postgres, need to discuss pros/cons of
    each approach
  - Need to look at how we generate the API documentation.  There is a
  swagger annotations project which allows us to document the API inside the
  REST resources (Jersey resources) and generate the swagger.json file
  automatically
  https://github.com/kongchen/swagger-maven-plugin
  - Records to be validated immediately when they are inserted into a
  dataset - this allows the validation (an expensive operation) to
  be load balanced if data is uploaded in chunks.
  - Need to determine how we are going to manage validation error
  definitions. Currently there are help snippets in the frontend but a good
  API design would also make these available via the API.  Don't really
  want to duplicate the content and have our content designer maintain two
  sets of documentation. (The intended design makes the validation error
  definitions available under the `/definitions` endpoint)
  - The frontend currently uploads a file to the backend, gets a
  unique identifier for the file and a validation result and stores this in
  redis, however with the new API design this data is also stored at the
  backend and the frontend could be updated to query the API rather than
  using redis.

## Terminology
### Dataset
A dataset represents a collection of data (requests) which can be manipulated
by making API calls to the appropriate resource.  A dataset could represent
the contents of a single file or a series of data from a database. Consumers
of this API may choose how they wish to use datasets based on their needs.

Operations on a dataset are performed by making API calls to the `/datasets`
path.

### Record
A dataset is composed of many requests. A recordEntity has a payload containing a
specific data-type.

Operations on a recordEntity are performed by making API calls to the
`/datasets/{dataset_id}/requests` path.

### Payload
The recordEntity payload, allows for the submission of different data types
through a common API.

Currently the only supported payload is the DataSample.

#### DataSample
A DataSample represents a specific abstractObservation or observation.  It is
currently the only type of recordEntity supported by this API.
In a DEP compliant CSV file this is a row of data.

### Definitions
The API may also be queried for definitions.  These describe various
properties about the API, such as the values that may be used for certain
fields of a DataSample or the description for a particular validation error.

#### Field Definitions
Available under `/definitions/{payload_type}/fields`

**TODO: Consider implications of extending current API field definitions to
all fields (even those without a controlled list) - returning information
such as the data type, the description of the field and its constraints
(e.g. the maximum length of a string field)**

Field definitions are currently available for:
- EA_ID
- Site_Name
- Parameter
- Rtn_Type
- Rtn_Period
- Meth_Stand
- Txt_Value
- Qualifier
- Ref_Period
- Unit

#### Validation Definitions
Available under `/definitions/{payload_type}/validation`

** TODO: Complete documentation**

## Versioning and backwards compatibility

The API has been designed to allow the functionality offered to be extended
without adversely affecting existing API consumers.
We use the [semantic versioning system](http://semver.org/).  This means
that if we make a change to the service that would prevent existing API
consumers from operating correctly then we shall use a new major version
number.  The API is published using the major version number in the
request path and any major change will be published alongside the
existing version of the API.

**TODO: Recommend agreeing/publishing a policy under which
previous versions of the API may be retired as indefinite support can
become very costly and hinder development of the service.  Is there a GDS
policy?**

## API Design

### Deployment Architecture
This API is load balanced (as illustrated below) and you should utilise
this when designing a consumer system.

```
                     _________________
                    |                 |
                    |  Clients        |
                    |_________________|
                            |
                            |
                            V
                     _________________
                    |                 |
                    |  Load Balancer  |
                    |_________________|
                        /   |   \
                       /    |    \
                      v     V     V
   _____________   _____________  _____________   _____________
  |             | |             ||             | |             |
  |  API Server | |  API Server ||  API Server | |  API Server |
  |_____________| |_____________||_____________| |_____________|
```
^ Not to scale

* If a consumer needs to submit a large dataset then the submission of data
should be performed in batches using multiple API calls.  The benefits of
this approach
are:
  *  Parallelism - if a large dataset is broken into smaller chunks then
  each chunk may be routed to a different API server and processed
  simultaneously with other chunks, effectively processing the dataset more
  quickly than if it was uploaded in a single chunk to a single API server.
  *  If a request fails (potentially due to a network issue) then the client
   only needs to retry submission for a small part of the entire dataset.


## API Requests

### Manipulating Objects using HTTP verbs
This service is designed using
[RESTful](http://en.wikipedia.org/wiki/Representational_state_transfer)
(Representational state transfer) principles.

Each endpoint (URL) may support one of four different http verbs.
* GET requests fetch information about an object
* PUT requests may be used to create or to update objects
* POST requests create objects with an endpoint preassigned by the server
* DELETE requests will delete objects.

### Request Headers

It is strongly recommended that consumers always specify the `Accept` header
when making requests to this API.  
The API is capable of serialization to both JSON and XML and the `Accept` 
header enables you to control the format to use.  We recommmend the use 
of the JSON format whenever possible.

Supported MIME types:

```
Accept: application/json

Accept: application/xml

```

Specifying the `Accept` header will also cause any well behaved network 
infrastructure device (such as a proxy) of the appropriate responses type
should that device intercept the request and return an error responses
directly.

### HATEOAS
[HATEOAS](https://en.wikipedia.org/wiki/HATEOAS), an abbreviation for
Hypermedia As The Engine Of Application State is a constraint of the
RESTful architecture.

Each API call responses contains hypermedia links which describe related
entities and provide the necessary information to navigate each
relationship.

An example of a typical responses containing hypermedia links under the
`links` key:
```json
{
  meta: {}
  data: {
    id: "record123",
    links: [
      {
        rel: "self",
        href: "https://report-landfill-data.service.gov.uk/api/v1/datasets/dataset123/requests/record123"
      },
      {
        rel: "dataset",
        href: "https://report-landfill-data.service.gov.uk/api/v1/datasets/dataset123"
      }
    ]
  }
}
```

Each link contains a pair of attributes:
- **rel** - the relationship of the target with regard to the source
- **href** - the URL used for the hyperlink

**TODO**
  - Document potential relationships in more detail
   - self
   - dataset
   - recordEntity
   - field
   - definition
   - etc
  -
  - https://jersey.java.net/documentation/latest/uris-and-links.html
  - Double check with Tom if AWS ELB strips X-Forwarded- headers:

  ```
  Spring HATEOAS respects various X-FORWARDED- headers. If you put a Spring
  HATEOAS service behind a proxy and properly configure it with
  X-FORWARDED-HOST headers, the resulting links will be properly formatted.
  ```

### Cache and concurrency control

The service provides support for caching and opt-in concurrency control
using the the HTTP/1.1 Conditional Requests specification ([RFC7232]
(https://tools.ietf.org/html/rfc7232))

### ETag (Entity Tag)
When an entity (such as a dataset or recordEntity) is created or modified, the API
will generate a unique hash to be associated with the entity. This provides
a unique fingerprint of the entity data and any change will result in a
completely new fingerprint being associated with the entity.
This fingerprint is known as an ETag (Entity Tag) within the HTTP/1.1
specification. An ETag is generated using a cryptographic hash function
such as [SHA-1](https://en.wikipedia.org/wiki/SHA-1).

This API also provides a number of collection-entities (such as `/datasets`
and `/datasets/sampledataset1/requests`).  Collection entities will also
provide an ETag which will change if the collection is modified in any way.
Modifying the data contained within a single collection entity will also
cause the collection ETag to change.  This is because a collection ETag
is generated using a hash of the combined hashes of every entry in the
collection.

The ETag for an entity is provided using the HTTP/1.1 `ETag` responses
header:

```
  ETag: "33a64df551425fcc55e4d42a148795d9f25f89d4"
```

### Conditional Queries

#### Conditional GET requests
A conditional GET request to an entity resource can be used to avoid
returning the full entity data in the responses body.

A conditional GET request is issued using the `If-None-Match` request
header.  This header should be set to the ETag value that the consumer
was sent when it previously fetched the requested entity.

```
  If-None-Match: "33a64df551425fcc55e4d42a148795d9f25f89d4"
```

When the API receives a request it will check if the hash provided in the
`If-None-Match` header matches the stored entity.  If the hashes match then
the entity has not been modified since the client last retrieved it and so
the server will respond with a `304 Not Modified` responses with an empty
body.  The client is then free to use the last version of the entity
it stored with the knowledge that no change has occurred on the
server.

#### Conditional PUT/POST requests
A conditional request to create or update an entity resource can be used to
avoid the 'lost update problem'.  Lost updates can occur when two different
clients attempt to modify the same recordEntity without checking for consistency
before writing:
  1. Client A reads record1
  2. Client B reads record1
  3. Client A changes and saves record1
  4. Client B changes and saves record1 (overwriting the change made by
  Client A)

A conditional PUT/POST request is issued using the `If-Match` request
header.  This header should be set to the ETag value that the consumer
was sent when it previously fetched the requested entity.
```
  If-Match: "33a64df551425fcc55e4d42a148795d9f25f89d4"
```
When the API receives a request it will check if the ETag hash
value provided in the `If-Match` header matches the stored entity.
  - If the ETag values do not match then the entity will not be modified
  and the server shall return a `412 Precondition Failed` responses.
  - If there is no existing entity with the given ETag then the server
  shall return a `412 Precondition Failed` responses and no entity will
  be created.

**TODO: RFC7232 also defines the `Last-Modified` responses header and the
`If-Modified-Since` and `If-Unmodified-Since` conditional request headers.
We need to decide whether to support these!**

## Responses

### The Envelope
Every responses is contained in a JSON envelope so that each responses has a
predictable set of keys with which you can interact:

```json
{
    "meta": {
        "code": 200
    },
    "data": {
        ...
    }
}
```

### Metadata
The meta key is used to communicate additional information about the
responses to the API consumer.

Currently, successful requests will only have a `code` key within the meta
block.  If in future a new feature (such as pagination) requires additional
information to be associated with the responses data then it shall be added
to the meta block.

Requests resulting in an error responses from the API will include additional
details about the error inside the `meta` object:

```json
{
        "meta": {
            "code": 400,
            "error_type": "OAuthException",
            "error_message": "..."
        }
}
```
---
**Note**

When implementing a responses handler you should consider that there are a
number of conditions where the responses may not originate from the API
server itself.  This can occur as a result of network infrastructure such
as a proxy server or load balancer.
For this reason API client implementations should not explicitly depend
on a JSON object being present in the responses body unless the code
code is in the `2xx` group.

## Character Set Support
** TODO: Jersey will likely support various charsets out of
the box when the client uses the Content-type request header.  We need to
investigate the extent of the support, whether data is converted to UTF-8
once it hits the service code and/or the implications on the
validation and the persistence layer.**

Example request header:
`Content-type: application/json; charset=utf-8`

## Response Headers
** TODO: List common responses headers (ETag, Date, Content-Type,
Content-Length **

## Response Codes
This API uses HTTP code codes to indicate the result of an API call to
the consumer.  The full set of HTTP code codes that may be used is defined
in [RFC7231, Section 6](https://tools.ietf.org/html/rfc7231#section-6).

Some of the more common (non-success) codes that you may encounter are
defined below:

| Code    | Description           | Condition |
|--------:| :----------------      | :--------------------------- |
| `400`   | `Bad Request`         | The request could not be understood by the server |
| `404`   | `Not Found`           | The identified resource could not be found |
| `412`   | `Precondition Failed` | A condition on the request was not satisfied, preventing the server from fulfilling the request |
| `413`   | `Request Entity Too Large` | The request exceeded the maximum  allowed request size configured on the server |
| `500`   | `Internal Server Error` | An unexpected condition prevented the server from fulfilling the request |

---
## Example Workflow

**TODO: Complete sample workflow with example data once we have the
completed data model **

General principles:

1. Add a new dataset
  * PUT /datasets/sampledataset
2. Add requests to the dataset (intending to extend this to allow upload
of multiple requests using a single PUT/POST call to the
/datasets/sampledataset/requests endpoint)
  * PUT /datasets/sampledataset/requests/record1
  * PUT /datasets/sampledataset/requests/record2
3. Check if the data is valid
  * GET /datasets/sampledataset/code
4. Look up validation definition (should be cached by the consumer)
  * GET /definitions/DataSample/validation/DR9050
5. Correct a recordEntity
  * PUT /datasets/sampledataset/requests/record2
6. Submit the dataset to the Environment Agency
  * PUT /datasets/sampledataset/code