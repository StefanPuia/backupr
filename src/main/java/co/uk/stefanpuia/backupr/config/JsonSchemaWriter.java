package co.uk.stefanpuia.backupr.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JsonSchemaWriter {

  @Value("classpath:/backupr.schema.json")
  private Resource schemaFile;

  public String generate() {
    try {
      return schemaFile.getContentAsString(StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
