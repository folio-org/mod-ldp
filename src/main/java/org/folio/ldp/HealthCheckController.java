package org.folio.ldp;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/admin/health")
public class HealthCheckController {
  @GetMapping
  ResponseEntity<?> checkHealth() {
    return ResponseEntity.status(200).body(null);
  }
  
}
