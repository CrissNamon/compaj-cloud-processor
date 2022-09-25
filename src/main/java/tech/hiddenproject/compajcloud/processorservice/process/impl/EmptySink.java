package tech.hiddenproject.compajcloud.processorservice.process.impl;

import java.util.function.LongConsumer;
import reactor.core.Disposable;
import reactor.core.publisher.FluxSink;
import reactor.util.context.Context;

/**
 * @author Danila Rassokhin
 */
public class EmptySink<T> implements FluxSink<T> {

  @Override
  public FluxSink<T> next(T t) {
    return this;
  }

  @Override
  public void complete() {

  }

  @Override
  public void error(Throwable throwable) {

  }

  @Override
  public Context currentContext() {
    return null;
  }

  @Override
  public long requestedFromDownstream() {
    return 0;
  }

  @Override
  public boolean isCancelled() {
    return true;
  }

  @Override
  public FluxSink<T> onRequest(LongConsumer longConsumer) {
    return null;
  }

  @Override
  public FluxSink<T> onCancel(Disposable disposable) {
    return null;
  }

  @Override
  public FluxSink<T> onDispose(Disposable disposable) {
    return null;
  }
}
