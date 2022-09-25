package tech.hiddenproject.compajcloud.processorservice.service;

import reactor.core.publisher.Flux;
import tech.hiddenproject.compajcloud.processorservice.data.ContainerEvent;

/**
 * @author Danila Rassokhin
 */
public interface OutputStream {

  Flux<ContainerEvent> read(String id);

}
