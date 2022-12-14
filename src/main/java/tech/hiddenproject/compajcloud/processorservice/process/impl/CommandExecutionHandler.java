package tech.hiddenproject.compajcloud.processorservice.process.impl;

import com.zaxxer.nuprocess.NuAbstractProcessHandler;
import com.zaxxer.nuprocess.NuProcess;
import java.nio.ByteBuffer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.FluxSink;

/**
 * @author Danila Rassokhin
 */
@Slf4j
public class CommandExecutionHandler extends NuAbstractProcessHandler {

  private NuProcess nuProcess;

  private Consumer<Integer> onStart;

  private Consumer<String> onEach;

  private Consumer<Integer> onStop;

  private Supplier<String> onIn;

  private Supplier<Boolean> closeIf;

  private FluxSink<String> sink;

  private boolean completed = false;

  public CommandExecutionHandler() {
    onStart = pid -> {};
    onEach = e -> {};
    onStop = code -> {};
    closeIf = () -> true;
    sink = new EmptySink<>();
  }

  @Override
  public void onStart(NuProcess nuProcess) {
    this.nuProcess = nuProcess;
    onStart.accept(nuProcess.getPID());
    if (onIn != null) {
      this.nuProcess.wantWrite();
    }
  }

  @Override
  public void onExit(int statusCode) {
    super.onExit(statusCode);
    stop(statusCode);
  }

  @Override
  public void onStdout(ByteBuffer buffer, boolean closed) {
    if (!closed) {
      byte[] bytes = new byte[buffer.remaining()];
      buffer.get(bytes);
      String output = new String(bytes);
      sink.next(output);
      onEach.accept(output);
      if (closeIf.get() && !completed) {
        nuProcess.closeStdin(true);
      } else if (onIn != null) {
        nuProcess.wantWrite();
      }
    }
  }

  @Override
  public boolean onStdinReady(ByteBuffer buffer) {
    if (onIn != null) {
      byte[] data = onIn.get().getBytes();
      buffer.put(data);
      buffer.flip();
    }
    return false;
  }

  public void addOnStart(Consumer<Integer> onStart) {
    this.onStart = onStart;
  }

  public void addOnIn(Supplier<String> onIn) {
    this.onIn = onIn;
  }

  public void addCloseIf(Supplier<Boolean> closeIf) {
    this.closeIf = closeIf;
  }

  public void stop(int statusCode) {
    if (statusCode == 0) {
      sink.complete();
    } else {
      sink.error(new Exception("Error: " + statusCode));
    }
    onStop.accept(statusCode);
    completed = true;
  }

  public void addOnStop(Consumer<Integer> onStop) {
    this.onStop = onStop;
  }

  public void addOnEach(Consumer<String> e) {
    this.onEach = e;
  }

  public void reactive(FluxSink<String> sink) {
    this.sink = sink;
  }
}
