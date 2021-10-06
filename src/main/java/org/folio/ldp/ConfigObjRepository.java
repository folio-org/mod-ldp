package org.folio.ldp;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface ConfigObjRepository extends CrudRepository<ConfigObj, ConfigObjId> {

  //magic derived query
  List<ConfigObj> findByTenant(String tenant);  
}
