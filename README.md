# mod-ldp

Current functionality is limited to retrieving all logs from the folio_release database, dbsystem.log table. See [application.yml](src/main/resources/application.yml) for configuration. 

#### Clone

```
git clone https://github.com/library-data-platform/mod-ldp.git
```

#### Run with hot-reload:


Set database password like so before running the server:

```
export SPRING_DATASOURCE_PASSWORD=yourPasswordHere
```

```
./mvnw spring-boot:run
```

Note that hot-reload can sometimes fail to detect changes in annotations (e.g. `@Data`), in which case a `clean` is needed to re-compile.

#### Package:
```
./mvnw clean package
```

which will output `target/ldp-0.0.1-SNAPSHOT.jar`

#### Run package:

Assuming you have already set the SPRING_DATASOURCE_PASSWORD as an environment variable:

```
java -jar target/ldp-0.0.1-SNAPSHOT.jar
```

The port can be changed by passing a command-line option `--server.port=8090`

## Install to Okapi locally

```
./mvnw generate-resources

cd scripts
./okapi-1-declare-mod.sh
./okapi-2-deploy-mod.sh
./okapi-3-enable-mod-for-tenant.sh
```

Assign permission to user ((doc)[https://github.com/folio-org/stripes-cli/blob/master/doc/user-guide.md#interacting-with-okapi]):

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
