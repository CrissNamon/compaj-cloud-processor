package tech.hiddenproject.compajcloud.processorservice.service.impl;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.hiddenproject.compajcloud.processorservice.data.ContainerEvent;
import tech.hiddenproject.compajcloud.processorservice.process.impl.IOExecutor;
import tech.hiddenproject.compajcloud.processorservice.process.impl.SequenceExecutor;
import tech.hiddenproject.compajcloud.processorservice.service.ContainerService;

/**
 * @author Danila Rassokhin
 */
@Service
@RefreshScope
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class DockerService implements ContainerService {

  private final Gson gson;

  @Value("${container-service.path}")
  private String dockerPath;

  @Value("${container-service.image}")
  private String dockerImage;

  @Value("${container-service.command}")
  private String dockerCommand;

  private Flux<ContainerEvent> dockerEventFlux;

  {
    dockerEventFlux = Flux.empty();
  }

  @Override
  public void create(String name) {
    IOExecutor runCmd = new IOExecutor(dockerPath, "run", "-id", "--name", name, dockerImage);
    IOExecutor execCmd = new IOExecutor(dockerPath, "exec", name, dockerCommand, "-s", "(0..10).each { println it }");
    IOExecutor stopCmd = new IOExecutor(dockerPath, "stop", name);
    IOExecutor removeCmd = new IOExecutor(dockerPath, "container", "rm", name);
    SequenceExecutor.of(runCmd, execCmd, stopCmd, removeCmd)
        .execute();
  }

  @Override
  public Mono<Boolean> isRunning(String name) {
    return IOExecutor.of(dockerPath, "ps", "-q", "-f", "status=running", "-f", "name=" + name)
        .sync()
        .next()
        .defaultIfEmpty("")
        .map(s -> !s.isEmpty());
  }

  @Override
  public Flux<ContainerEvent> watch() {
    return dockerEventFlux;
  }

  @Override
  public String getName(String userId) {
    return userId;
  }

  @Override
  public Flux<ContainerEvent> watchFile(String containerName, String path) {
    return IOExecutor.of(dockerPath, "exec", containerName, "tail", "-f", "-n", "+1", path)
        .sync()
        .doFinally(signalType -> log.trace("SIGNAL: " + signalType.toString()))
        .doOnComplete(() -> log.info("COMPLETED FILE WATCH"))
        .map(line -> ContainerEvent.fromCmd(containerName, line));
  }

  @Override
  public Mono<Boolean> exec(String containerName, String cmd) {
    return IOExecutor.of(
            dockerPath, "exec", containerName, "sh", "-c", dockerCommand + " -s " + cmd + " >> ./compaj/" + containerName,
            " && echo true"
        )
        .sync()
        .onErrorReturn("ERROR")
        .last("")
        .map(String::isEmpty);
  }

  @EventListener(ApplicationReadyEvent.class)
  public void startWatcher() {
    dockerEventFlux = IOExecutor.of(
            dockerPath, "events",
            "-f", "event=start",
            "-f", "event=stop",
            "--format", "{{json .}}"
        )
        .onStart(pid -> log.info("STARTED DOCKER WATCHER"))
        .async()
        .filter(json -> json.trim().length() > 2)
        .map(this::dockerEventMapper);
  }

  private ContainerEvent dockerEventMapper(String json) {
    try {
      return gson.fromJson(
          json.trim(),
          ContainerEvent.class
      );
    } catch (Exception e) {
      log.error("Error occurred while parsing docker event: " + json.trim(), e);
      return new ContainerEvent();
    }
  }
}
