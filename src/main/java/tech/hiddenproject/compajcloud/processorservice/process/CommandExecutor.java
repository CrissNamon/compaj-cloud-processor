package tech.hiddenproject.compajcloud.processorservice.process;

import java.util.function.Consumer;
import java.util.function.Supplier;
import reactor.core.publisher.Flux;

/**
 * @author Danila Rassokhin
 */
public interface CommandExecutor {
  CommandExecutor onStart(Consumer<Integer> event);
  CommandExecutor onIn(Supplier<String> data);
  CommandExecutor closeIf(Supplier<Boolean> condition);
  CommandExecutor onEach(Consumer<String> event);
  CommandExecutor onStop(Action event);
  Flux<String> async();
  Flux<String> sync();
}
