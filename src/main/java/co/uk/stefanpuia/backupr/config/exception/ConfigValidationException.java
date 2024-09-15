package co.uk.stefanpuia.backupr.config.exception;

public class ConfigValidationException extends RuntimeException {
  public ConfigValidationException(String message) {
    super(message);
  }
}
