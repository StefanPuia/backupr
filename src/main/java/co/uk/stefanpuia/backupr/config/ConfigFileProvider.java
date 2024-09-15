package co.uk.stefanpuia.backupr.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Path;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class ConfigFileProvider {
  private static final String DEFAULT_CONFIG_FILE_NAME = ".backupr.json";
  private static final String DEFAULT_LOCATION = System.getProperty("user.home");

  private final EnvironmentReader environmentReader;

  protected String getDefaultLocation() {
    return Path.of(DEFAULT_LOCATION, DEFAULT_CONFIG_FILE_NAME).toAbsolutePath().toString();
  }

  private String getConfigLocation() {
    return environmentReader.getConfigLocation().orElseGet(this::getDefaultLocation);
  }

  public InputStream getConfigInputStream() throws FileNotFoundException {
    final var location = getConfigLocation();
    log.info("Reading config file at '{}'", location);

    return new FileInputStream(location);
  }
}
