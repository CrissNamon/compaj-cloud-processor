package tech.hiddenproject.compajcloud.processorservice.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Danila Rassokhin
 */
@Configuration
public class ContainerConfig {

  @Bean
  public Gson gson() {
    return new GsonBuilder().registerTypeAdapter(
        LocalDateTime.class,
        (JsonDeserializer<LocalDateTime>) (json, type, jsonDeserializationContext) -> {
          Instant instant = Instant.ofEpochMilli(json.getAsJsonPrimitive().getAsLong());
          return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        }
    ).create();
  }

}
