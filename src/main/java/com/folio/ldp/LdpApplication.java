package com.folio.ldp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class LdpApplication {

  private static final Logger log = LoggerFactory.getLogger(LdpApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(LdpApplication.class, args);
	}


  @Bean
  public CommandLineRunner demo(LogObjRepository repository) {
    return (args) -> {

      // fetch all customers
      log.info("Customers found with findAll():");
      log.info("-------------------------------");
      for (LogObj logObj : repository.findAll()) {
        log.info(logObj.toString());
      }
      log.info("");

    };
  }

}
