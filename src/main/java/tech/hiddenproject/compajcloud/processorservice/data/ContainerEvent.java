package tech.hiddenproject.compajcloud.processorservice.data;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Danila Rassokhin
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContainerEvent {

  private String status;

  private String id;

  private LocalDateTime time;

  private String action;

  public static ContainerEvent fromCmd(String containerName, String result) {
    return new ContainerEvent("exec", containerName, LocalDateTime.now(), result);
  }
}
