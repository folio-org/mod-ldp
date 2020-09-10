package org.folio.ldp;

import java.util.List;

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


  // @Bean
  // public CommandLineRunner demo(TableObjRepository tableObjRepository) {
  //   return (args) -> {

  //     // fetch all customers
  //     log.info("Customers found with findAll():");
  //     log.info("-------------------------------");
  //     for (TableObj tableObj : tableObjRepository.findAll()) {
  //       log.info(tableObj.toString());
  //     }
  //     log.info("");

  //   };
  // }

}
