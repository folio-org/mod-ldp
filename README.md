# mod-ldp

Copyright (C) 2020-2021 The Open Library Foundation

This software is distributed under the terms of the Apache License,
Version 2.0. See the file "[LICENSE](LICENSE)" for more information.


## Overview

`mod-ldp` is a FOLIO module that mediates access to the Library Data Platform (LDP). It removes the need to deal directly with a relational database by providing a simple WSAPI that can be used by UI code such as [`ui-ldp`](https://github.com/library-data-platform/ui-ldp).

The WSAPI is described in machine-readable form by [a RAML file](ramls/ldp.raml) and [its associated JSON Schemas and example documents](ramls). Auto-generated human-readable documentation will in due course become available at https://dev.folio.org/reference/api/ but until then it can be read, in two different but equivalent forms, at:
* https://s3.amazonaws.com/foliodocs/api/mod-ldp/ldp.html
* https://s3.amazonaws.com/foliodocs/api/mod-ldp/p/ldp.html

(There is also undocmented and incomplete functionality to retrieve logs from the `folio_release` database, `dbsystem.log` table.)

See [`application.yml`](src/main/resources/application.yml) for configuration. 

## Clone, build, run

```
$ git clone https://github.com/library-data-platform/mod-ldp.git
$ cd mod-ldp
$ mvn install
$ java -jar target/mod-ldp-0.0.1-SNAPSHOT.jar
```

The port can be changed by passing a command-line option `--server.port=8090`

If the password has not been configured in the [`application.yml`](src/main/resources/application.yml) file, it can be provided at run-time in the `SPRING_DATASOURCE_PASSWORD` environment variable. So:
```
env SPRING_DATASOURCE_PASSWORD=swordfish123 java -jar target/mod-ldp-0.0.1-SNAPSHOT.jar
```

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
