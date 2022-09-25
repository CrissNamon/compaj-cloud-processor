package tech.hiddenproject.compajcloud.processorservice.controller;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.hiddenproject.compajcloud.processorservice.data.ContainerEvent;
import tech.hiddenproject.compajcloud.processorservice.service.ContainerService;
import tech.hiddenproject.compajcloud.processorservice.service.OutputStream;

/**
 * @author Danila Rassokhin
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class ProcessorController {

  private final ContainerService containerService;

  private final OutputStream fileOutputStream;

  private static <T> Mono<ServerResponse> keepAlive(Duration duration, Flux<T> data) {
    Flux<ServerSentEvent<T>> heartBeat = Flux.interval(duration)
        .map(
            e -> ServerSentEvent.<T>builder()
                .comment("keep alive")
                .build()
        )
        .doOnEach(serverSentEventSignal -> log.info("PING"))
        .doFinally(signalType -> log.info("SIGNAL FROM HEARTBEAT: " + signalType));
    return ServerResponse.ok()
        .contentType(MediaType.TEXT_EVENT_STREAM)
        .body(Flux.merge(heartBeat, data), ServerSentEvent.class);
  }

  public Mono<ServerResponse> watch(ServerRequest serverRequest) {
    return keepAlive(Duration.ofSeconds(2), containerService.watch());
  }

  public Mono<ServerResponse> watchContainer(ServerRequest serverRequest) {
    String id = serverRequest.pathVariable("id");
    Flux<ServerSentEvent<ContainerEvent>> events = fileOutputStream.read(id)
        .doOnSubscribe(subscription -> log.info("Subscribed to file: " + subscription))
        .map(containerEvent -> ServerSentEvent.<ContainerEvent>builder().data(containerEvent).build())
        .doOnComplete(() -> log.info("COMPLETED SSE"))
        .doFinally(signalType -> log.info("FINALlY SSE: " + signalType))
        .timeout(Duration.ofSeconds(3600));
    return keepAlive(Duration.ofSeconds(2), events);
  }

  public Mono<ServerResponse> exec(ServerRequest serverRequest) {
    String id = serverRequest.pathVariable("id");
    return serverRequest.bodyToMono(String.class)
        .flatMap(cmd -> ServerResponse.ok()
            .body(containerService.exec(id, cmd), Boolean.class)
        );
  }

}
