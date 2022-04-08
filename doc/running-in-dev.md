# Building and running `mod-ldp` for development


## Building the module

Use `mvn install`. If it fails with errors like this:
> [ERROR] ColumnObjControllerTest » IllegalState Could not find a valid Docker environment
It's probably because your user doesn't have permission to use Docker. You can "fix" this by running as root, but better is to add your user to the `docker` group:
```
sudo usermod -a -G docker mike
```
Then you can log out and in again to have it take effect, or use `newgrp docker` to explicitly tell your present shell that you have the new group. `mvn install` should now work.


## Finding the LDP datasbase

The name of the host that contains the folio-snapshot LDP database unfortunately changes on each rebuild. It is always for the form `ec2-NUMBER-NUMBER-NUMBER-NUMBER.compute-1.amazonaws.com` and can be found as follows:

* Go to https://dev.folio.org/guides/automation/#reference-environments
* Choose "folio-snapshot" (or "folio-snapshot-2" if you prefer) in the left-hand ToC.
* Follow the "See Jenkins job: folio-snapshot" link.
* Go to the most recent build in the "Build History" left-hand panel.
* Select "View as plain text" in the left-hand panel.
* Do browser "Find in page" to look for: `ec2-`

Shortcut: go straight to [the console text from the latest folio-snapshot build](https://jenkins-aws.indexdata.com/job/FOLIO_Reference_Builds/job/folio-snapshot/lastBuild/consoleText)


## Testing the connection to the LDP database

Once you have found the current hostname, you can test it with the command-line Postgres client:
```
psql -h ec2-34-224-78-21.compute-1.amazonaws.com -U ldp ldp
Password: diku_ldp9367
```
Try this query:
```
select * from public.user_users limit 1;
```


## Running the module

Once you have found the current hostname, you can start mod-ldp as follows:
```
env DB_HOST=ec2-34-224-78-21.compute-1.amazonaws.com DB_PORT=5432 DB_DATABASE=ldp DB_USERNAME=ldp DB_PASSWORD=diku_ldp9367 java -jar target/mod-ldp-1.0.3-SNAPSHOT.jar --server.port=12370
```

(The top-level README says to use `DB_NAME`, `DB_USER` and `DB_PASS`. XXX Fix the README.)

You can test that it's running correctly with something like:
```
curl -H 'X-Okapi-Tenant: diku' localhost:12370//ldp/config/dbinfo
```
This will not actually work, as the necessary configuration entries are not in place until the module has been inserted into Okapi hand had its tenant intialization interface invoked, but you should at least get an informative response like "No such key dbinfo".


## Initializing the module

You will need to insert database information for mod-ldp to run usefully:
```
curl -X PUT -H 'X-Okapi-Tenant: diku' -H 'Content-type: application/json' localhost:12370/ldp/config/dbinfo -d '{ "tenant": "diku", "key": "dbinfo", "value": "{ \"url\": \"jdbc:postgresql://ec2-34-224-78-21.compute-1.amazonaws.com:5432/ldp\", \"user\": \"ldp\", \"pass\": \"diku_ldp9367\" }" }'
```
You can verify that this has worked using:
```
curl -H 'X-Okapi-Tenant: diku' localhost:12370/ldp/config/dbinfo
```
Now you should be able to have mod-ldp fetch the list of tables, using:
```
curl -H 'X-Okapi-Tenant: diku' localhost:12370/ldp/db/tables
```

## Plumbing

There are 3 properties in use at least. port, http.port, server.port , If you lucky the README or DeploymentDescriptor-template.json will tell. The Dockerfile most often assumes a default listening port.

XXX -Dhttp.port=1234

