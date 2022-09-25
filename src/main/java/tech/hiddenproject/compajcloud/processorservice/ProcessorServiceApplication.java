package tech.hiddenproject.compajcloud.processorservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableWebFlux
@EnableEurekaClient
public class ProcessorServiceApplication {

  public static void main(String[] args) {

    SpringApplication.run(ProcessorServiceApplication.class, args);

  }
}
