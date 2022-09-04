package tech.hiddenproject.compajcloud.processorservice.process.impl;

import java.util.ArrayList;
import java.util.List;
import tech.hiddenproject.compajcloud.processorservice.process.CommandExecutor;
import tech.hiddenproject.compajcloud.processorservice.process.PipelineExecutor;

/**
 * @author Danila Rassokhin
 */
public class SequenceExecutor implements PipelineExecutor {

  private final List<CommandExecutor> cmds;

  public SequenceExecutor(CommandExecutor first, CommandExecutor... next) {
    cmds = new ArrayList<>();
    cmds.add(first);
    cmds.addAll(List.of(next));
    for (int i = 0; i < cmds.size() - 1; i++) {
      int finalI = i;
      cmds.get(i).onStop(() -> cmds.get(finalI + 1).sync().subscribe());
    }
  }

  @Override
  public void execute() {
    cmds.get(0).sync().subscribe();
  }

  public static PipelineExecutor of(CommandExecutor first, CommandExecutor... next) {
    return new SequenceExecutor(first, next);
  }
}
