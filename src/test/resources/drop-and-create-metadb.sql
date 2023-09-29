DROP SCHEMA IF EXISTS metadb CASCADE;

CREATE SCHEMA IF NOT EXISTS metadb;

CREATE TABLE IF NOT EXISTS metadb.base_table(
  schema_name VARCHAR(63),
  table_name VARCHAR(63),
  transformed BOOL,
  parent_schema_name VARCHAR(63),
  parent_table_name VARCHAR(63),
  source_name VARCHAR(63)
  );

DROP SCHEMA IF EXISTS folio_users CASCADE;

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

CREATE TABLE IF NOT EXISTS folio_users.users__t(
  __id int8,
  __start timestamptz,
  __end timestamptz,
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

  INSERT INTO metadb.base_table VALUES (
    --schema_name
    'folio_users',
    --table_name
    'users__t',
    --transformed
    true,
    --parent_schema_name
    'folio_users',
    --parent_table_name
    'users',
    --source_name
    'folio'
  );

  INSERT INTO metadb.base_table VALUES (
    --schema_name
    'folio_users',
    --table_name
    'users',
    --transformed
    false,
    --parent_schema_name
    null,
    --parent_table_name
    null,
    --source_name
    'folio'
  );

  INSERT INTO folio_users.users ("__id","__start","__end","__current","__origin",id,"jsonb",creation_date,created_by,patrongroup) VALUES
  	 (1,'2023-08-31 11:58:02.437','9999-12-30 19:00:00.000',true,'','d6f42256-d4ea-4dc4-91ee-16dde6d6752d'::uuid,'{"id": "d6f42256-d4ea-4dc4-91ee-16dde6d6752d", "active": true, "metadata": {"createdDate": "2023-05-22T21:03:32.113Z", "updatedDate": "2023-05-22T21:03:32.113Z", "createdByUserId": "91f5c819-27ea-567a-8167-8b5ad672124e", "updatedByUserId": "91f5c819-27ea-567a-8167-8b5ad672124e"}, "personal": {"email": "", "lastName": "System User", "addresses": [], "firstName": "ERM Harvester"}, "proxyFor": [], "username": "id_erm_harvester_systemuser", "createdDate": "2023-05-22T21:03:32.145+00:00", "departments": [], "updatedDate": "2023-05-22T21:03:32.145+00:00"}','2023-05-22 21:03:32.112','91f5c819-27ea-567a-8167-8b5ad672124e'::uuid,NULL);

  INSERT INTO folio_users.users ("__id","__start","__end","__current","__origin",id,"jsonb",creation_date,created_by,patrongroup) VALUES
  	 (2,'2023-08-31 11:58:02.437','9999-12-30 19:00:00.000',true,'','6ea7d73f-a178-4d4c-b717-0878b5b28076'::uuid,'{"id": "6ea7d73f-a178-4d4c-b717-0878b5b28076", "active": true, "metadata": {"createdDate": "2022-04-29T17:39:13.124", "updatedDate": "2022-04-29T17:39:13.124+00:00"}, "personal": {"lastName": "System", "addresses": []}, "proxyFor": [], "username": "pubsub_user", "createdDate": "2022-04-29T17:39:13.163+00:00", "departments": [], "updatedDate": "2022-04-29T17:39:13.163+00:00"}','2022-04-29 17:39:13.124',NULL,NULL);

  INSERT INTO folio_users.users ("__id","__start","__end","__current","__origin",id,"jsonb",creation_date,created_by,patrongroup) VALUES
  	 (3,'2023-08-31 11:58:02.437','9999-12-30 19:00:00.000',true,'','9847d716-71a5-41d8-9da5-fb1ed0c3cb08'::uuid,'{"id": "9847d716-71a5-41d8-9da5-fb1ed0c3cb08", "active": true, "metadata": {"createdDate": "2022-04-29T17:39:39.148", "updatedDate": "2022-04-29T17:39:39.148+00:00"}, "personal": {"lastName": "System", "addresses": []}, "proxyFor": [], "username": "mod-search", "createdDate": "2022-04-29T17:39:39.160+00:00", "departments": [], "updatedDate": "2022-04-29T17:39:39.160+00:00"}','2022-04-29 17:39:39.148',NULL,NULL);


  INSERT INTO folio_users.users__t ("__id","__start","__end","__current","__origin",id,updated_date,username,created_date,active,patron_group,barcode,expiration_date,enrollment_date,external_system_id,"type") VALUES
  	 (1,'2023-08-31 11:58:02.437','9999-12-30 19:00:00.000',true,'','d6f42256-d4ea-4dc4-91ee-16dde6d6752d'::uuid,'2023-05-22 17:03:32.145','id_erm_harvester_systemuser','2023-05-22 17:03:32.145',true,NULL,NULL,NULL,NULL,NULL,NULL),
  	 (2,'2023-08-31 11:58:02.437','9999-12-30 19:00:00.000',true,'','6ea7d73f-a178-4d4c-b717-0878b5b28076'::uuid,'2022-04-29 13:39:13.163','pubsub_user','2022-04-29 13:39:13.163',true,NULL,NULL,NULL,NULL,NULL,NULL),
  	 (3,'2023-08-31 11:58:02.437','9999-12-30 19:00:00.000',true,'','9847d716-71a5-41d8-9da5-fb1ed0c3cb08'::uuid,'2022-04-29 13:39:39.160','mod-search','2022-04-29 13:39:39.160',true,NULL,NULL,NULL,NULL,NULL,NULL);

