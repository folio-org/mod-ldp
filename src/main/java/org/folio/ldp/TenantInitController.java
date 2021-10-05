package org.folio.ldp;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "_tenant")
public class TenantInitController {
  @PostMapping ResponseEntity<?> initializeTenant( @RequestBody TenantInitData tid,
   @RequestHeader HttpHeaders headers ) {
    String tenantId = headers.getFirst("X-Okapi-Tenant");
    System.out.println("Tenant initialization for tenant " + tenantId);
    System.out.println("module to version " + tid.module_to);
    System.out.println("module from version " + tid.module_from);
    return ResponseEntity.status(201).body(null);
  }

  
}
