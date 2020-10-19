package org.folio.ldp;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ColumnObjRepository extends JpaRepository<ColumnObj, String> {

  @Query("FROM #{#entityName} " +
         "WHERE tableSchema = 'public' " +
         "AND tableName = ?1 " +
         "AND columnName != 'data' ")
  List<ColumnObj> findByTableName(String table);

}