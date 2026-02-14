package co.uk.stefanpuia.backupr;

import static com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_COMMENTS;

import co.uk.stefanpuia.backupr.shell.ShellExceptionResolver;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.shell.command.annotation.CommandScan;

@CommandScan
@Configuration
@AllArgsConstructor
public class BackuprApplicationConfiguration {
  @Bean
  public ObjectMapper objectMapper() {
    return new ObjectMapper().registerModule(new JavaTimeModule()).enable(ALLOW_COMMENTS);
  }

  @Bean
  public ShellExceptionResolver shellExceptionResolver() {
    return new ShellExceptionResolver();
  }
}
