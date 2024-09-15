package co.uk.stefanpuia.backupr;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class BackuprApplicationConfiguration {
  @Bean
  public ObjectMapper objectMapper() {
    return new ObjectMapper();
  }
}
