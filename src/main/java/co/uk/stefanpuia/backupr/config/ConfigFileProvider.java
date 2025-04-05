package co.uk.stefanpuia.backupr.config;

import co.uk.stefanpuia.backupr.config.exception.ConfigFileNotFoundException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Path;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class ConfigFileProvider {
  private static final String DEFAULT_CONFIG_FILE_NAME = ".backupr.jsonc";
  private static final String DEFAULT_LOCATION = System.getProperty("user.home");

  private final EnvironmentReader environmentReader;

  protected String getDefaultLocation() {
    final String defaultLocation =
        Path.of(DEFAULT_LOCATION, DEFAULT_CONFIG_FILE_NAME).toAbsolutePath().toString();
    log.debug("Using default config path: '{}'", defaultLocation);
    return defaultLocation;
  }

  private String getConfigLocation() {
    return environmentReader.getConfigLocation().orElseGet(this::getDefaultLocation);
  }

  public String resolveConfigPath(final String configPath) {
    return Strings.isNotBlank(configPath) ? configPath : getConfigLocation();
  }

  public InputStream getConfigInputStream(final String configPath)
      throws ConfigFileNotFoundException {
    try {
      final var location = resolveConfigPath(configPath);
      log.debug("Reading config file at '{}'", location);

      return new FileInputStream(location);
    } catch (final FileNotFoundException e) {
      throw new ConfigFileNotFoundException(configPath);
    }
  }
}
