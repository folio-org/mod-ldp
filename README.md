# mod-ldp

Copyright (C) 2020-2023 The Open Library Foundation

This software is distributed under the terms of the Apache License,
Version 2.0. See the file "[LICENSE](LICENSE)" for more information.


## Overview

`mod-ldp` is a FOLIO module that mediates access to the [Library Data Platform](https://github.com/library-data-platform/ldp) (LDP). It removes the need to deal directly with a relational database by providing a simple WSAPI that can be used by UI code such as [`ui-ldp`](https://github.com/library-data-platform/ui-ldp).

The WSAPI is described in machine-readable form by [a RAML file](ramls/ldp.raml) and [its associated JSON Schemas and example documents](ramls). Auto-generated human-readable documentation is [provided](https://dev.folio.org/reference/api/#mod-ldp) in two different but equivalent forms:
* https://s3.amazonaws.com/foliodocs/api/mod-ldp/ldp.html
* https://s3.amazonaws.com/foliodocs/api/mod-ldp/p/ldp.html

(There is also undocumented and incomplete functionality to retrieve logs from the `folio_release` database, `dbsystem.log` table.)

See [`application.yml`](src/main/resources/application.yml) for configuration.



## Clone, build, run

```
$ git clone https://github.com/library-data-platform/mod-ldp.git
$ cd mod-ldp
$ mvn install
$ java -jar target/mod-ldp-0.0.1-SNAPSHOT.jar
```

The port can be changed by passing a command-line option `--server.port=8090`

The default configuration for database connection can be changed in the [`application.yml`](src/main/resources/application.yml) file. It currently looks for run-time provided environmental variables of DB_HOST, DB_PORT, DB_DATABASE, DB_USERNAME and DB_PASSWORD.

If running `mod-ldp` locally, you will likely run into CORS problems with Stripes refusing to make GET and POST requests to it because OPTIONS requests don't return the necessary `Access-control-allow-origin` header. To work around this, you can run a CORS-permissive HTTP proxy such as [`local-cors-anywhere`](https://github.com/dkaoster/local-cors-anywhere) -- which by default listens on port 8080 -- and access the running `mod-ldp` at http://localhost:8080/http://localhost:8001.

It's also possible to run with hot-reload (although note that hot-reload can sometimes fail to detect changes in annotations (e.g. `@Data`), in which case a `clean` is needed to re-compile):

```
./mvnw spring-boot:run
```

## Install to Okapi locally

```
./mvnw generate-resources

cd scripts
./okapi-1-declare-mod.sh
./okapi-2-deploy-mod.sh
./okapi-3-enable-mod-for-tenant.sh
```

[Assign permission to the user](https://github.com/folio-org/stripes-cli/blob/master/doc/user-guide.md#interacting-with-okapi):

```
stripes okapi login diku_admin --okapi http://localhost:9130 --tenant diku
stripes perm assign --name ldp.read --user diku_admin --okapi http://localhost:9130 --tenant diku
```


## Build and run with Docker

Build the jar:

```
mvn package
```

Build the docker image:

```
docker build -t mod-ldp .
```

Run the docker container:

```
docker run -p 8001:8001 -e SPRING_DATASOURCE_PASSWORD=yourPasswordHere --rm mod-ldp
```

## Additional information

In order to talk to an external LDP database, mod-ldp needs to have db connection information configured on a per-tenant basis. This is accomplished by creating an entry at the config endpoint with a key of 'dbinfo'.

The relevant fields of the configuration are 'user', 'pass' and 'url'.

An example would be to PUT the following payload to /ldp/config/dbinfo:
```
{
  "key":"dbinfo",
  "tenant":"diku",
  "value":"{ \"user\" : \"ldp_user\", \"url\" : \"jdbc:postgresql:\/\/somehost.net:5432\/db_name\", \"pass\" : \"abc123def456\" }"  
}
```

### Other documentation

[Library Data Platform](https://github.com/library-data-platform/ldp) (LDP)
-- an open source platform for analytics in libraries.

[ui-ldp](https://github.com/library-data-platform/ui-ldp)
-- LDP query builder UI for FOLIO/ReShare

### Code of Conduct

Refer to the Wiki [FOLIO Code of Conduct](https://wiki.folio.org/display/COMMUNITY/FOLIO+Code+of+Conduct).

### Issue tracker

See project [MODLDP](https://issues.folio.org/browse/MODLDP)
at the [FOLIO issue tracker](https://dev.folio.org/guidelines/issue-tracker).

### Code analysis

FOLIO [SonarQube analysis](https://sonarcloud.io/dashboard?id=org.folio%3Amod-ldp).

### Download and configuration

The built artifacts for this module are available.

* FOLIO notes:
[configuration](https://dev.folio.org/download/artifacts) for repository access,
and the [Docker image](https://hub.docker.com/r/folioorg/mod-ldp/).

