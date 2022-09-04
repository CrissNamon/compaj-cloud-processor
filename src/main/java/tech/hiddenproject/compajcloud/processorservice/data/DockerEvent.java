package tech.hiddenproject.compajcloud.processorservice.data;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * @author Danila Rassokhin
 */
@Data
public class DockerEvent {
  private String status;
  private String id;
  private LocalDateTime time;
}
