package tech.hiddenproject.compajcloud.processorservice.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.io.File;
import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @author Danila Rassokhin
 */
@Configuration
public class ContainerConfig {

  @Bean
  @Scope("prototype")
  public ProcessBuilder processBuilder() {
    return new ProcessBuilder()
        .directory(new File(System.getProperty("user.home")));
  }

  @Bean
  public Gson gson() {
    return new GsonBuilder().registerTypeAdapter(LocalDateTime.class,
        (JsonDeserializer<LocalDateTime>) (json, type, jsonDeserializationContext) -> {
          Instant instant = Instant.ofEpochMilli(json.getAsJsonPrimitive().getAsLong());
          return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        }).create();
  }

}
