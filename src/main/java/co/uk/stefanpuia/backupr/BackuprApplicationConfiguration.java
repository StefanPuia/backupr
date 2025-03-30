package co.uk.stefanpuia.backupr;

import static com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_COMMENTS;

import co.uk.stefanpuia.backupr.shell.ShellExceptionResolver;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    return new ObjectMapper().enable(ALLOW_COMMENTS);
  }

  @Bean
  public ShellExceptionResolver shellExceptionResolver() {
    return new ShellExceptionResolver();
  }
}
