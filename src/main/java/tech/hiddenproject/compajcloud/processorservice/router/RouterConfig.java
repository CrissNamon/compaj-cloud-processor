package tech.hiddenproject.compajcloud.processorservice.router;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import tech.hiddenproject.compajcloud.processorservice.controller.ProcessorController;

/**
 * @author Danila Rassokhin
 */
@Configuration
public class RouterConfig {

  @Bean
  public RouterFunction<?> route(ProcessorController processorController) {
    return RouterFunctions.route(
        RequestPredicates.GET("/watch"),
        processorController::watch
    );
  }

  @Bean
  public RouterFunction<?> exec(ProcessorController processorController) {
    return RouterFunctions.route(
        RequestPredicates.GET("/exec/{id}"),
        processorController::exec
    );
  }

  @Bean
  public RouterFunction<?> watch(ProcessorController processorController) {
    return RouterFunctions.route(
        RequestPredicates.GET("/file/{id}"),
        processorController::watchContainer
    );
  }
}
