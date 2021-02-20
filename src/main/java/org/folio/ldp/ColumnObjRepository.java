package org.folio.ldp;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

// Security warning: this allows the user to find the column metadata of any
// schema-table combination in the database

public interface ColumnObjRepository extends JpaRepository<ColumnObj, String> {
  
  @Query("FROM #{#entityName} " +
         "WHERE tableSchema = ?1 " +
         "AND tableName = ?2 " +
         "AND columnName != 'data' ")
  List<ColumnObj> findByTableName(String schema, String table);
}
