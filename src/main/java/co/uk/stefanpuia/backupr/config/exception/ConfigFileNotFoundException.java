package co.uk.stefanpuia.backupr.config.exception;

public class ConfigFileNotFoundException extends RuntimeException {
  public ConfigFileNotFoundException(final String configPath) {
    super("Configuration file not found at '%s'".formatted(configPath));
  }
}
