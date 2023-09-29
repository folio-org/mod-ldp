CREATE SCHEMA IF NOT EXISTS metadb;

CREATE TABLE IF NOT EXISTS metadb.base_table(
  schema_name VARCHAR(63),
  table_name VARCHAR(63),
  transformed BOOL,
  parent_schema_name VARCHAR(63),
  parent_table_name VARCHAR(63),
  source_name VARCHAR(63)
  );

CREATE SCHEMA IF NOT EXISTS folio_users;

CREATE TABLE IF NOT EXISTS folio_users.users(
  __id int8,
  __start timestamptz,
  __end timestamptz,
  __current BOOL,
  __origin VARCHAR(63),
  id uuid,
  jsonb jsonb,
  creation_date timestamp,
  created_by uuid,
  patrongroup uuid
  );

CREATE TABLE IF NOT EXISTS folio_users.users_t(
  __id int8,
  __start timestampz,
  __end timestampz,
  __current BOOL,
  __origin VARCHAR(63),
  id uuid,
  updated_date timestamptz,
  username text,
  created_date timestamptz,
  active BOOL,
  patron_group uuid,
  barcode text,
  expiration_date timestamptz,
  enrollment_date timestamptz,
  external_system_id text,
  type text
  );
