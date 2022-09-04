package tech.hiddenproject.compajcloud.processorservice.service.impl;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.hiddenproject.compajcloud.processorservice.data.DockerEvent;
import tech.hiddenproject.compajcloud.processorservice.entity.ContainerEntity;
import tech.hiddenproject.compajcloud.processorservice.process.impl.IOExecutor;
import tech.hiddenproject.compajcloud.processorservice.service.ContainerService;
import tech.hiddenproject.compajcloud.processorservice.process.impl.SequenceExecutor;

/**
 * @author Danila Rassokhin
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class DockerService implements ContainerService {

  private final Gson gson;

  private Flux<DockerEvent> dockerEventFlux;

  {
    dockerEventFlux = Flux.empty();
  }

  @Override
  public ContainerEntity create(String name) {
    IOExecutor runCmd = new IOExecutor("/usr/local/bin/docker", "run", "-id", "--name", name, "kpekepsalt/compaj-repl:latest");
    IOExecutor execCmd = new IOExecutor("/usr/local/bin/docker", "exec", name, "compaj", "-s", "(0..10).each { println it }")
        .onEach(s -> log.info("EXEC CMD RESULT: " + s.trim()));
    IOExecutor stopCmd = new IOExecutor("/usr/local/bin/docker", "stop", name);
    IOExecutor removeCmd = new IOExecutor("/usr/local/bin/docker", "container", "rm", name);
    SequenceExecutor.of(runCmd, execCmd, stopCmd, removeCmd)
        .execute();
    return null;
  }

  @Override
  public Mono<Boolean> isRunning(String name) {
    return IOExecutor.of("/usr/local/bin/docker", "ps", "-q", "-f", "status=running", "-f", "name="+name)
        .onStart(pid -> log.info("Started check"))
        .onStop(() -> log.info("Completed check"))
        .sync().next().defaultIfEmpty("").map(s -> !s.isEmpty());
  }

  @EventListener(ApplicationReadyEvent.class)
  public void startWatcher() {
    dockerEventFlux = IOExecutor.of(
        "/usr/local/bin/docker", "events",
            "-f", "event=start",
            "-f", "event=stop",
            "--format", "{{json .}}")
        .onStart(pid -> log.info("Started docker watcher"))
        .async()
        .filter(json -> json.trim().length() > 2)
        .map(this::dockerEventMapper);
  }

  @Override
  public Flux<DockerEvent> watch() {
    return dockerEventFlux;
  }

  private DockerEvent dockerEventMapper(String json) {
    try {
      return gson.fromJson(
          json.trim(),
          DockerEvent.class
      );
    } catch (Exception e) {
      log.info("RAW JSON: " + json.trim());
      return new DockerEvent();
    }
  }
}
