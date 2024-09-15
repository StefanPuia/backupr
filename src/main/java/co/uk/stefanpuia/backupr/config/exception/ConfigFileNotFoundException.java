package co.uk.stefanpuia.backupr.config.exception;

public class ConfigFileNotFoundException extends RuntimeException {
  public ConfigFileNotFoundException(final String message) {
    super(message);
  }
}
