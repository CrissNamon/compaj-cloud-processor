package tech.hiddenproject.compajcloud.processorservice.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.hiddenproject.compajcloud.processorservice.data.ContainerEvent;

/**
 * @author Danila Rassokhin
 */
public interface ContainerService {

  void create(String name);

  Mono<Boolean> isRunning(String name);

  Flux<ContainerEvent> watch();

  String getName(String userId);

  Flux<ContainerEvent> watchFile(String containerName, String path);

  Mono<Boolean> exec(String containerName, String cmd);

}
