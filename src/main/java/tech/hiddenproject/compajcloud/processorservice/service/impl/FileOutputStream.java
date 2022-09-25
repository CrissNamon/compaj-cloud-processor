package tech.hiddenproject.compajcloud.processorservice.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import tech.hiddenproject.compajcloud.processorservice.data.ContainerEvent;
import tech.hiddenproject.compajcloud.processorservice.service.ContainerService;
import tech.hiddenproject.compajcloud.processorservice.service.OutputStream;

/**
 * @author Danila Rassokhin
 */
@Slf4j
@Service
public class FileOutputStream implements OutputStream {

  private final ContainerService containerService;

  @Value("${container-service.logDirectory}")
  private String logDirectory;

  @Autowired
  public FileOutputStream(ContainerService containerService) {
    this.containerService = containerService;
  }

  @Override
  public Flux<ContainerEvent> read(String id) {
    return containerService.watchFile(id, getFilePath(id));
  }

  private String getFilePath(String id) {
    return logDirectory + "/" + id;
  }
}
