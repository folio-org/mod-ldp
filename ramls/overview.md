The Library Data Platform (LDP) API provides simple mediated access to an LDP database hosted elsewhere (as configured in the bundle's `application.yml` file). It provides only three entry points, each of them very simple:

1. `/ldp/db/tables`: request a list of all the tables in their various schemas
2. `/ldp/db/columns`: Request a list of all the columns in a specified table. (The schema and table names are povided as URL query parameters)
3. `/ldp/db/query`: Submit a query

Four types are defined to support these operations:
* The first operation returns [`tables`](tables-schema.json), a list of table-and-schema-name pairs.
* The second operation returns [`columns`](columns-schema.json), a list of column definitions including information such as the column name and type.
* The third operation accepts a [`query`](query-schema.json), a set of parameters such as the table to search in, the criteria, and the columns to return.
* The third operation returns [`results`](results-schema.json), a list of objects representing rows that satisfy the query, each containing the specified set of columns.

