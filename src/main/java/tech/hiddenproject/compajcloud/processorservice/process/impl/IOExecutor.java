package tech.hiddenproject.compajcloud.processorservice.process.impl;

import com.zaxxer.nuprocess.NuProcessBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import reactor.core.publisher.Flux;
import tech.hiddenproject.compajcloud.processorservice.process.CommandExecutor;

/**
 * @author Danila Rassokhin
 */
public class IOExecutor implements CommandExecutor {

  private final NuProcessBuilder processBuilder;

  private final CommandExecutionHandler processHandler;

  public IOExecutor(String cmd, String... args) {
    List<String> fullCommand = new ArrayList<>();
    fullCommand.add(cmd);
    fullCommand.addAll(List.of(args));
    processHandler = new CommandExecutionHandler();
    processBuilder = new NuProcessBuilder(fullCommand);
    processBuilder.setProcessListener(processHandler);
  }

  public static IOExecutor of(String cmd, String... args) {
    return new IOExecutor(cmd, args);
  }

  @Override
  public IOExecutor onStart(Consumer<Integer> event) {
    processHandler.addOnStart(event);
    return this;
  }

  @Override
  public IOExecutor onIn(Supplier<String> data) {
    processHandler.addOnIn(data);
    return this;
  }

  @Override
  public IOExecutor closeIf(Supplier<Boolean> condition) {
    processHandler.addCloseIf(condition);
    return this;
  }

  @Override
  public IOExecutor onEach(Consumer<String> event) {
    processHandler.addOnEach(event);
    return this;
  }

  @Override
  public IOExecutor onStop(Consumer<Integer> event) {
    processHandler.addOnStop(event);
    return this;
  }

  @Override
  public Flux<String> async() {
    return Flux.push(processHandler::reactive)
        .doFirst(processBuilder::start);
  }

  @Override
  public Flux<String> sync() {
    return Flux.push(processHandler::reactive)
        .doOnSubscribe(s -> processBuilder.start());
  }

}
