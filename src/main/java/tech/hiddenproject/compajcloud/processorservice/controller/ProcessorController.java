package tech.hiddenproject.compajcloud.processorservice.controller;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import reactor.core.publisher.Mono;
import tech.hiddenproject.compajcloud.processorservice.data.DockerEvent;
import tech.hiddenproject.compajcloud.processorservice.process.impl.IOExecutor;
import tech.hiddenproject.compajcloud.processorservice.service.ContainerService;

/**
 * @author Danila Rassokhin
 */
@RestController
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class ProcessorController {

  private ContainerService containerService;

  @GetMapping("/create")
  public void createContainer() throws IOException, InterruptedException {
    containerService.create("Container_1");
  }

  @GetMapping("/check")
  public Mono<Boolean> checkContainer() {
    return containerService.isRunning("Container_1");
  }

  @GetMapping("/watch")
  public DeferredResult<DockerEvent> watch() {
    DeferredResult<DockerEvent> result = new DeferredResult<>();
    result.setResult(new DockerEvent());
    containerService.watch()
        .subscribe(result::setResult);
    return result;
  }

  @GetMapping("/test")
  public void test() {
    AtomicInteger i = new AtomicInteger(0);
    IOExecutor.of("/bin/cat")
        .onStart(pid -> log.info("Started cat cmd"))
        .onIn(() -> String.valueOf(i.get()))
        .closeIf(() -> i.getAndIncrement() > 4)
        .onStop(() -> log.info("Stopped cat cmd"))
        .sync()
        .doOnNext(log::info)
        .subscribe();
  }

}
