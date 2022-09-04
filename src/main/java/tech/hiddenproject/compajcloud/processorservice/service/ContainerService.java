package tech.hiddenproject.compajcloud.processorservice.service;

import java.io.IOException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.hiddenproject.compajcloud.processorservice.data.DockerEvent;
import tech.hiddenproject.compajcloud.processorservice.entity.ContainerEntity;

/**
 * @author Danila Rassokhin
 */
public interface ContainerService {

  ContainerEntity create(String name) throws IOException, InterruptedException;
  Mono<Boolean> isRunning(String name);

  Flux<DockerEvent> watch();

}
