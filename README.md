# mod-ldp

Current functionality is limited to retrieving all logs from the folio_release database, ldpsystem.log table. See [application.yml](src/main/resources/application.yml) for configuration. 

#### Install:
```
git clone https://github.com/library-data-platform/mod-ldp.git
```

Set password like so before running any commands:

```
export SPRING_DATASOURCE_PASSWORD=yourPasswordHere
```

#### Run with hot-reload:
```
./mvnw spring-boot:run
```

Note that hot-reload can sometimes fail to detect changes in annotations (e.g. `@Data`), in which case a `clean` is needed to re-compile.

#### Package:
```
./mvnw clean package
```

which will output `target/ldp-0.0.1-SNAPSHOT.jar` that can be run with:

```
java -jar target/ldp-0.0.1-SNAPSHOT.jar
```
