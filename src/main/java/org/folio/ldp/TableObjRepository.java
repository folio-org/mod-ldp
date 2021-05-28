package org.folio.ldp;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TableObjRepository extends JpaRepository<TableObj, String> {

  @Query("SELECT t "
         + "FROM TableObj t "
         + "WHERE t.tableSchema = 'local' "
         + "OR t.tableSchema = 'folio_reporting' "
         + "OR t.tableSchema = 'public' "
         + "ORDER by t.tableSchema ")
  List<TableObj> getAllTablesBySchema();
}
